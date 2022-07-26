package cn.bctools.oss.component;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.oss.cons.SystemCons;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.dto.Etag;
import cn.bctools.oss.props.OssProperties;
import cn.bctools.oss.template.OssTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 〈文件上传〉
 *
 * @author auto
 * @since 1.0.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class FileUploadComponent {
    OssProperties ossProperties;
    OssTemplate ossTemplate;

    StringRedisTemplate redisTemplate;

    /**
     * 〈执行上传服务器操作 + 入库记录〉
     *
     * @since: 1.0.0
     * @author: auto
     */
    public BaseFile doUpload(MultipartFile file, String module, String bucketName) {
        BaseFile baseFile = getTargetTemplate().putFile(bucketName, module, file.getOriginalFilename(), file);
        return baseFile;
    }

    /**
     * 〈获取文件流〉
     *
     * @since: 1.0.0
     * @author: auto
     */
    public InputStream getObject(String bucketName, String fileName) {
        return getTargetTemplate().getObject(bucketName, fileName);
    }

    /**
     * 〈获取目标service〉
     *
     * @since: 1.0.0
     * @author: auto
     */
    public OssTemplate getTargetTemplate() {
        return ossTemplate;
    }

    /**
     * 获取文件外链
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public String fileLink(String bucketName, String fileName) {
        OssTemplate targetTemplate = getTargetTemplate();
        return targetTemplate.fileLink(fileName, bucketName);
    }

    /**
     * 创建分段上传
     *
     * @param bucketName      桶
     * @param filename        文件名
     * @param totalPartNumber 总分段数量
     * @return 凭证
     */
    public Etag createMultipartUpload(String bucketName, String filename, Integer totalPartNumber) {
        Assert.isTrue(totalPartNumber >= 4, "错误的分片数量，至少4片。");
        OssTemplate targetTemplate = this.getTargetTemplate();
        String fileName = targetTemplate.getFileName("bigfile", filename);
        String uploadId = targetTemplate.createMultipartUpload(bucketName, fileName);
        Etag etag = new Etag().setUploadId(uploadId).setBucketName(bucketName).setOriginFileName(filename).setFileName(fileName).setTotalPartNumber(totalPartNumber).setPartNumber(0).setCreateTime(System.currentTimeMillis());
        String key = String.format(SystemCons.R_KEY, uploadId);
        redisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(etag));
        redisTemplate.expire(key, 180, TimeUnit.MINUTES);
        log.info("创建分段上传：bucketName={}，filename={}，uploadId={},totalPartNumber={},", bucketName, fileName, uploadId, totalPartNumber);
        return etag;
    }

    /**
     * 上传分段
     * 加锁防止并发
     *
     * @param bytes      文件内容
     * @param bucketName 桶
     * @param partNumber 分片序号
     * @param uploadId   凭证
     */
    public String uploadPart(byte[] bytes, String bucketName, Integer partNumber, String uploadId) {
        log.info("进入分段上传:bucketName={},uploadId={},size={},partNumber={},", bucketName, uploadId, bytes.length, partNumber);
        OssTemplate targetTemplate = this.getTargetTemplate();
        String redisKey = String.format(SystemCons.R_KEY, uploadId);
        List<String> range = redisTemplate.opsForList().range(redisKey, 0, -1);
        Etag etag = Optional.ofNullable(range).orElse(Collections.emptyList()).stream().map(f0 -> JSONUtil.toBean(f0, Etag.class)).filter(f0 -> f0.getPartNumber() == 0).findFirst().orElseThrow(() -> new BusinessException("凭证已过期"));
        log.info("获取到凭证信息:etag={}", JSONUtil.toJsonStr(etag));
        String deltaKey = redisKey.concat(":delta");
        try {
            if (bytes.length > SystemCons.MIN_MULTIPART_SIZE) {
                throw new BusinessException("分段大小不能超过：" + SystemCons.MIN_MULTIPART_SIZE);
            }

            Assert.isTrue(StrUtil.equals(bucketName, etag.getBucketName()), "无效的凭证");
            Assert.isTrue(StrUtil.equals(uploadId, etag.getUploadId()), "无效的凭证");

            Etag part = targetTemplate.uploadPart(bytes, bucketName, partNumber, etag.getFileName(), uploadId);
            String jsonStr = JSONUtil.toJsonStr(part);
            redisTemplate.opsForList().leftPush(redisKey, jsonStr);
            log.info("push分段信息入缓存库：redisKey={},part={}", redisKey, jsonStr);
            List<String> partList = redisTemplate.opsForList().range(redisKey, 0, -1);
            log.info("获取已push的所有分段信息：redisKey={},partList={}", redisKey, JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(partList, Collections.emptyList())));
            if (!ObjectUtils.isEmpty(partList) && (partList.size() - 1) == etag.getTotalPartNumber()) {
                log.info("检测到所有分段已上传完毕：TotalPartNumber={}", etag.getTotalPartNumber());
                Long delta = redisTemplate.opsForValue().increment(deltaKey, 1);
                redisTemplate.expire(deltaKey, 10, TimeUnit.MINUTES);
                if (Objects.equals(delta, 1L)) {
                    //合并
                    Set<Etag> set = partList.stream().map(f0 -> JSONUtil.toBean(f0, Etag.class)).filter(f0 -> f0.getPartNumber() != 0).collect(Collectors.toSet());
                    targetTemplate.completeMultipartUpload(bucketName, etag.getFileName(), uploadId, set);
                    redisTemplate.delete(Arrays.asList(redisKey, deltaKey));
                    return targetTemplate.fileLink(etag.getFileName(), bucketName);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("分段上传错误", e);
            targetTemplate.abortMultipartUpload(bucketName, etag.getFileName(), uploadId);
            redisTemplate.delete(Arrays.asList(redisKey, deltaKey));
            throw new BusinessException("分段上传失败:" + e.getMessage());
        }
    }

}
