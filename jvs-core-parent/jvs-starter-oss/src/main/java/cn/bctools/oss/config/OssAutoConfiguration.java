package cn.bctools.oss.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.bctools.oss.component.FileUploadComponent;
import cn.bctools.oss.controller.FileUploadController;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.mapper.SysFileMapper;
import cn.bctools.oss.po.OssFile;
import cn.bctools.oss.props.OssProperties;
import cn.bctools.oss.service.FileDataInterface;
import cn.bctools.oss.template.AliOssTemplate;
import cn.bctools.oss.template.MinioTemplate;
import cn.bctools.oss.template.OssTemplate;
import cn.bctools.oss.utils.ThumbnailUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * aws 自动配置类
 *
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoConfiguration {

    private OssProperties ossProperties;

    @Bean
    @ConditionalOnMissingBean(FileDataInterface.class)
    public FileDataInterface FileDataInterface(SysFileMapper fileMapper) {
        return new FileDataInterface() {

            @Override
            public void insert(BaseFile baseFile) {
                OssFile entity = new OssFile()
                        .setBucketName(baseFile.getBucketName())
                        .setCreateTime(LocalDateTime.now())
                        .setFileName(baseFile.getFileName())
                        .setFileType(baseFile.getFileType())
                        .setModule(baseFile.getModule())
                        .setSize(baseFile.getSize())
                        .setFilePath(baseFile.getFileName())
                        .setLabel(SystemThreadLocal.get("label"));
                fileMapper.insert(entity);
                log.info("上传成功一个文件:{}", JSONObject.toJSONString(baseFile));
            }

            @Override
            public void newBucket(String bucketName) {
                log.info("创建一个新的桶:{}", bucketName);
            }

            @Override
            public Page page(Page<OssFile> page, String fileName, String fileType, Long startTime, Long endTime, String label) {
                OssTemplate ossTemplate = SpringContextUtil.getBean(OssTemplate.class);
                LambdaQueryWrapper<OssFile> queryWrapper = Wrappers.<OssFile>lambdaQuery()
                        .eq(ObjectNull.isNotNull(label), OssFile::getLabel, label)
                        .like(StrUtil.isNotBlank(fileType), OssFile::getFileType, fileType);
                queryWrapper.like(StrUtil.isNotBlank(fileName), OssFile::getFileName, fileName);
                queryWrapper.between(ObjectUtil.isNotNull(startTime) && ObjectUtil.isNotNull(endTime), OssFile::getCreateTime, startTime, endTime);
                queryWrapper.orderByDesc(OssFile::getCreateTime);
                Page<OssFile> ossFilePage = fileMapper.selectPage(page, queryWrapper);
                ossFilePage.getRecords().forEach(e -> {
                    String fileLink = ossTemplate.fileLink(e.getFileName(), e.getBucketName());
                    e.setFileLink(fileLink);
                    e.setThumbnail(ThumbnailUtil.getImageBase64(e.getFileType()));
                });
                return ossFilePage;
            }

            @Override
            public List<String> buckets() {
                List<OssFile> minioFiles = fileMapper.selectList(Wrappers.<OssFile>lambdaQuery().groupBy(OssFile::getBucketName).select(OssFile::getBucketName));
                return Optional.ofNullable(minioFiles).orElse(Collections.emptyList()).stream().map(OssFile::getBucketName).filter(StrUtil::isNotBlank).collect(Collectors.toList());

            }

            @Override
            public List<String> fileTypes(String bucketName) {
                LambdaQueryWrapper<OssFile> minioFileLambdaQueryWrapper = Wrappers.<OssFile>lambdaQuery();
                if (StrUtil.isNotBlank(bucketName)) {
                    minioFileLambdaQueryWrapper.eq(StrUtil.isNotBlank(bucketName), OssFile::getBucketName, StrUtil.trim(bucketName));
                }
                minioFileLambdaQueryWrapper
                        .isNotNull(OssFile::getFileType)
                        .groupBy(OssFile::getFileType)
                        .select(OssFile::getFileType);
                List<OssFile> minioFiles = fileMapper.selectList(minioFileLambdaQueryWrapper);
                return Optional.ofNullable(minioFiles).orElse(Collections.emptyList()).stream().map(OssFile::getFileType).filter(StrUtil::isNotBlank).map(String::toLowerCase).distinct().collect(Collectors.toList());
            }

            @Override
            public List<String> fileLabel(String fileLabel) {
                LambdaQueryWrapper<OssFile> minioFileLambdaQueryWrapper = Wrappers.<OssFile>lambdaQuery();
                if (StrUtil.isNotBlank(fileLabel)) {
                    minioFileLambdaQueryWrapper.eq(StrUtil.isNotBlank(fileLabel), OssFile::getLabel, StrUtil.trim(fileLabel));
                }
                minioFileLambdaQueryWrapper
                        .isNotNull(OssFile::getLabel)
                        .groupBy(OssFile::getLabel)
                        .select(OssFile::getLabel);
                List<OssFile> minioFiles = fileMapper.selectList(minioFileLambdaQueryWrapper);
                return Optional.ofNullable(minioFiles).orElse(Collections.emptyList()).stream().map(OssFile::getLabel).filter(StrUtil::isNotBlank).map(String::toLowerCase).distinct().collect(Collectors.toList());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(OSSClient.class)
    @ConditionalOnProperty(value = "oss.name", havingValue = "alioss")
    public OSSClient ossClient() {
        // 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
        ClientConfiguration conf = new ClientConfiguration();
        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        conf.setMaxConnections(1024);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        conf.setSocketTimeout(50000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        conf.setConnectionTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        conf.setConnectionRequestTimeout(1000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        conf.setIdleConnectionTime(60000);
        // 设置失败请求重试次数，默认为3次。
        conf.setMaxErrorRetry(5);
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, conf);
    }

    @Bean
    @ConditionalOnProperty(value = "oss.name", havingValue = "alioss")
    @ConditionalOnBean({OSSClient.class})
    public OssTemplate aliOssTemplate(OSSClient ossClient, FileDataInterface fileDataInterface) {
        return new AliOssTemplate(ossClient, fileDataInterface, ossProperties);
    }

    @Bean
    @ConditionalOnProperty(value = "oss.name", havingValue = "minio")
    public OssTemplate template(FileDataInterface fileDataInterface) {
        return new MinioTemplate(ossProperties, fileDataInterface);
    }

    @Bean
    FileUploadComponent fileUploadComponent(OssTemplate ossTemplate, StringRedisTemplate redisTemplate) {
        return new FileUploadComponent(ossProperties, ossTemplate, redisTemplate);
    }

    @Bean
    public FileUploadController ossEndpoint(FileUploadComponent fileUploadComponent, FileDataInterface fileDataInterface) {
        return new FileUploadController(fileUploadComponent, fileDataInterface);
    }
}
