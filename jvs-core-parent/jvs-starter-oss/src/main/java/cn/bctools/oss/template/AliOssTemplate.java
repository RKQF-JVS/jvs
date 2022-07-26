package cn.bctools.oss.template;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.dto.Etag;
import cn.bctools.oss.props.OssProperties;
import cn.bctools.oss.service.FileDataInterface;
import cn.bctools.oss.utils.FilePathUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈AliossTemplate〉
 *
 * @since: 1.0.0
 * @author: auto
 */
@AllArgsConstructor
@Slf4j
public class AliOssTemplate implements OssTemplate {

    OSSClient ossClient;
    FileDataInterface fileDataInterface;
    OssProperties ossProperties;

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    @Override
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            fileDataInterface.newBucket(bucketName);
            ossClient.createBucket(bucketName);
        }
    }

    /**
     * 存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @Override
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return ossClient.doesBucketExist(bucketName);
    }

    /**
     * 获取文件外链
     *
     * @param fileName   文件名称
     * @param bucketName 源存储桶
     * @return String
     */
    @Override
    @SneakyThrows
    public String fileLink(String fileName, String bucketName) {
        //默认7天
        int i = Math.min(7 * 24, ObjectUtil.defaultIfNull(ossProperties.getTimelinessHour(), 7 * 24)) * 3600000;
        Date date = new Date(System.currentTimeMillis() + i);
        String fileLink = ossClient.generatePresignedUrl(bucketName, fileName, date).toString();
        if (ossProperties.getPublicBuckets().contains(bucketName)) {
            int endIndex = fileLink.indexOf("?");
            if (endIndex > 0) {
                return fileLink.substring(0, endIndex);
            }
        }
        return fileLink;
    }

    @Override
    public String fileLink(String fileName) {
        return fileLink(fileName, SpringContextUtil.getApplicationContextName());
    }

    /**
     * 上传文件
     *
     * @param bucketName   存储桶名称
     * @param originalName 上传文件名
     * @param file         文件
     * @return BaseFile
     */
    @Override
    @SneakyThrows
    public BaseFile putFile(String bucketName, String module, String originalName, MultipartFile file) {
        return putFile(bucketName, module, originalName, file.getInputStream());
    }

    /**
     * 上传文件
     *
     * @param bucketName   存储桶名称
     * @param originalName 上传文件名
     * @param inputStream  文件
     * @return BaseFile
     */
    @Override
    @SneakyThrows
    public BaseFile putFile(String bucketName, String module, String originalName, InputStream inputStream) {
        return put(bucketName, module, inputStream, originalName, false);
    }

    /**
     * 上传文件
     *
     * @param bucketName   存储桶名称
     * @param stream       上传文件InputStream
     * @param originalName 上传文件key
     * @param cover        是否覆盖
     * @return BaseFile
     */
    @Override
    @SneakyThrows
    public BaseFile put(String bucketName, String module, InputStream stream, String originalName, boolean cover) {
        Long size = Long.valueOf(stream.available());
        makeBucket(bucketName);
        String key = getFileName(module, originalName);
        // 覆盖上传
        if (cover) {
            ossClient.putObject(bucketName, key, stream);
        } else {
            PutObjectResult response = ossClient.putObject(bucketName, key, stream);
            int retry = 0;
            int retryCount = 5;
            while (ObjectNull.isNull(response.getETag()) && retry < retryCount) {
                response = ossClient.putObject(bucketName, key, stream);
                retry++;
            }
        }
        BaseFile file = new BaseFile();
        file.setOriginalName(originalName);
        file.setFileName(key);
        file.setBucketName(bucketName);
        file.setFileType(FilePathUtils.getMinorMediaType(originalName));
        file.setModule(module);
        file.setSize(size);
        fileDataInterface.insert(file);
        return file;
    }


    /**
     * 删除文件
     *
     * @param fileName   文件名称
     * @param bucketName 存储桶名称
     */
    @Override
    @SneakyThrows
    public void removeFile(String bucketName, String fileName) {
        ossClient.deleteObject(bucketName, fileName);
    }

    /**
     * 获取文件流
     *
     * @param bucketName 存储桶
     * @param fileName   文件名称
     * @return
     */
    @Override
    public InputStream getObject(String bucketName, String fileName) {
        OSSObject object = ossClient.getObject(bucketName, fileName);
        return object.getObjectContent();
    }

    @Override
    public List<BaseFile> listFiles(String bucketName) {
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        List<OSSObjectSummary> ossObjectSummaries = Optional.ofNullable(objectListing.getObjectSummaries()).orElse(Collections.emptyList());
        if (ObjectUtil.isEmpty(ossObjectSummaries)) {
            return Collections.emptyList();
        }
        return ossObjectSummaries.stream().map(map -> new BaseFile().setFileName(map.getKey()).setBucketName(bucketName).setOriginalName(map.getKey())).collect(Collectors.toList());
    }

    @Override
    public String createMultipartUpload(String bucketName, String filename) {
        try {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, filename);
            InitiateMultipartUploadResult response = ossClient.initiateMultipartUpload(request);
            String uploadId = response.getUploadId();
            log.info("创建分段上传成功,bucketName={}, filename={},uploadId={}", bucketName, filename, uploadId);
            return uploadId;
        } catch (Exception e) {
            log.error("创建分段上传失败,bucketName={}, filename={}", bucketName, filename, e);
            throw new BusinessException("创建分段上传失败:{}", e.getMessage());
        }
    }

    @Override
    public Etag uploadPart(byte[] bytes, String bucketName, Integer partNumber, String filename, String uploadId) {
        try {
            UploadPartRequest request = new UploadPartRequest(bucketName, filename, uploadId, partNumber, new ByteArrayInputStream(bytes), bytes.length);
            UploadPartResult uploadPartResponse = ossClient.uploadPart(request);
            log.info("分段上传成功:bucketName={}, filename={},partNumber={}", bucketName, filename, partNumber);
            return new Etag().setEtag(uploadPartResponse.getETag()).setPartNumber(partNumber).setBucketName(bucketName).setFileName(filename).setUploadId(uploadId);
        } catch (Exception e) {
            log.info("分段上传失败:bucketName={}, filename={},partNumber={}", bucketName, filename, partNumber, e);
            throw new BusinessException("分段上传失败：{}", e.getMessage());
        }
    }

    @Override
    public void completeMultipartUpload(String bucketName, String filename, String uploadId, Set<Etag> etagList) {
        try {
            List<PartETag> partETags = etagList.stream().filter(e -> StrUtil.isNotBlank(e.getEtag())).map(e -> new PartETag(e.getPartNumber(), e.getEtag())).sorted(Comparator.comparingLong(PartETag::getPartNumber)).collect(Collectors.toList());
            CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, filename, uploadId, partETags);
            CompleteMultipartUploadResult result = ossClient.completeMultipartUpload(request);
            log.info("合并分段返回={}", result.toString());
            log.info("合并分段成功：bucketName={}, filename={}", bucketName, filename);
        } catch (Exception e) {
            log.error("合并分段失败：bucketName={}, filename={}", bucketName, filename, e);
            throw new BusinessException("合并分段失败:{}", e.getMessage());
        }

    }

    @Override
    public void abortMultipartUpload(String bucketName, String filename, String uploadId) {
        try {
            AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, filename, uploadId);
            ossClient.abortMultipartUpload(request);
            log.info("终止分段成功：bucketName={}, filename={}", bucketName, filename);
        } catch (Exception e) {
            log.error("终止分段失败：bucketName={}, filename={}", bucketName, filename, e);
            throw new BusinessException("终止分段失败:{}", e.getMessage());
        }
    }

    @Override
    public BaseFile putFile(String originalName, InputStream inputStream, String... catalogue) {
        String module = Arrays.stream(catalogue).collect(Collectors.joining(StringPool.SLASH));
        return putFile(SpringContextUtil.getApplicationContextName(), module, originalName, inputStream);

    }

    @Override
    public BaseFile putContent(String originalName, String content, String... catalogue) {
        byte[] serialize = ObjectUtil.serialize(content);
        String module = Arrays.stream(catalogue).collect(Collectors.joining(StringPool.SLASH));
        return putFile(SpringContextUtil.getApplicationContextName(), module, originalName, new ByteArrayInputStream(serialize));
    }

}
