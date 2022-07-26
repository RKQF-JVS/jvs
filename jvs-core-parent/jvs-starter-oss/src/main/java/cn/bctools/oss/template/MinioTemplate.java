package cn.bctools.oss.template;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.dto.Etag;
import cn.bctools.oss.props.OssProperties;
import cn.bctools.oss.service.FileDataInterface;
import cn.bctools.oss.utils.FilePathUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import io.minio.messages.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * minio 交互类
 *
 * @author
 */
@Slf4j
public class MinioTemplate extends MinioClient implements OssTemplate {
    private final FileDataInterface fileDataInterface;
    private final OssProperties ossProperties;

    public MinioTemplate(OssProperties ossProperties, FileDataInterface fileDataInterface) {
        super(MinioClient.builder().endpoint(ossProperties.getEndpoint().trim().startsWith("http") ? ossProperties.getEndpoint() : "http://" + ossProperties.getEndpoint()).credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey()).build());
        this.ossProperties = ossProperties;
        this.fileDataInterface = fileDataInterface;
    }

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
            this.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
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
        return this.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
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
        String fileLink = this.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .method(Method.GET)
                .expiry(Math.min(7 * 24, ObjectUtil.defaultIfNull(ossProperties.getTimelinessHour(), 7 * 24)), TimeUnit.HOURS)
                .build());
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
     * @param bucketName 存储桶名称
     * @param fileName   上传文件名
     * @param file       文件
     * @return BaseFile
     */
    @Override
    @SneakyThrows
    public BaseFile putFile(String bucketName, String module, String fileName, MultipartFile file) {
        return putFile(bucketName, module, fileName, file.getInputStream());
    }

    /**
     * 上传文件
     *
     * @param bucketName  存储桶名称
     * @param fileName    上传文件名
     * @param inputStream 文件
     * @return BaseFile
     */
    @Override
    @SneakyThrows
    public BaseFile putFile(String bucketName, String module, String fileName, InputStream inputStream) {
        return put(bucketName, module, inputStream, fileName, false);
    }

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param stream     上传文件InputStream
     * @param key        上传文件key
     * @param cover      是否覆盖
     * @return BaseFile
     */
    @Override
    @SneakyThrows
    public BaseFile put(String bucketName, String module, InputStream stream, String key, boolean cover) {
        Long size = Long.valueOf(stream.available());
        makeBucket(bucketName);
        String originalName = key;
        key = getFileName(module, key);
        this.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .contentType(FilePathUtils.getMinorMediaType(key))
                .stream(stream, stream.available(), PutObjectArgs.MIN_MULTIPART_SIZE)
                .build());

        BaseFile file = new BaseFile();
        file.setOriginalName(originalName);
        file.setFileName(key);
        file.setBucketName(bucketName);
        file.setFileType(FilePathUtils.getMinorMediaType(originalName));
        file.setModule(module);
        file.setSize(size);
        //记录信息
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
        this.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param fileName   文件名称
     * @return 二进制流
     */
    @SneakyThrows
    @Override
    public InputStream getObject(String bucketName, String fileName) {
        return this.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

    @Override
    public List<BaseFile> listFiles(String bucketName) {
        Iterable<Result<Item>> results;
        try {
            results = this.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            return Collections.emptyList();
        }
        List<BaseFile> list = new ArrayList<>(0);
        results.forEach(map -> {
            Item item = null;
            try {
                item = map.get();
            } catch (Exception e) {
                return;
            }
            list.add(new BaseFile().setFileName(item.objectName()).setOriginalName(item.objectName()).setBucketName(bucketName));
        });
        return list;
    }

    @Override
    public String createMultipartUpload(String bucketName, String filename) {
        try {
            CreateMultipartUploadResponse response = this.createMultipartUpload(bucketName, null, filename, null, null);
            String uploadId = response.result().uploadId();
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
            UploadPartResponse uploadPartResponse = this.uploadPart(
                    bucketName,
                    null,
                    filename,
                    bytes,
                    bytes.length,
                    uploadId,
                    partNumber,
                    null, null);
            log.info("分段上传成功:bucketName={}, filename={},partNumber={}", bucketName, filename, partNumber);
            return new Etag().setEtag(uploadPartResponse.etag()).setPartNumber(partNumber).setBucketName(bucketName).setFileName(filename).setUploadId(uploadId);
        } catch (Exception e) {
            log.info("分段上传失败:bucketName={}, filename={},partNumber={}", bucketName, filename, partNumber, e);
            //抛出具体错误信息
            throw new BusinessException("分段上传失败:{}", e.getMessage());
        }
    }

    @Override
    public void completeMultipartUpload(String bucketName, String filename, String uploadId, Set<Etag> etagList) {
        try {
            Part[] parts = etagList.stream().filter(e -> StrUtil.isNotBlank(e.getEtag())).map(e -> new Part(e.getPartNumber(), e.getEtag())).sorted(Comparator.comparingLong(Part::partNumber)).toArray(Part[]::new);
            ObjectWriteResponse response = this.completeMultipartUpload(bucketName, null, filename, uploadId, parts, null, null);
            log.info("合并分段返回={}", response.toString());
            log.info("合并分段成功：bucketName={}, filename={}", bucketName, filename);
        } catch (Exception e) {
            log.error("合并分段失败：bucketName={}, filename={}", bucketName, filename, e);
            throw new BusinessException("合并分段失败:{}", e.getMessage());
        }

    }

    @Override
    public void abortMultipartUpload(String bucketName, String filename, String uploadId) {
        try {
            this.abortMultipartUpload(bucketName, null, filename, uploadId, null, null);
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
