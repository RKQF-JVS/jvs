package cn.bctools.oss.template;

import cn.bctools.common.utils.IdGenerator;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.dto.Etag;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 〈OssTemplate〉
 *
 * @author auto
 * @since 1.0.0
 */
public interface OssTemplate {

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    void makeBucket(String bucketName);

    /**
     * 存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 判断结果
     */
    boolean bucketExists(String bucketName);

    /**
     * 获取文件外链
     *
     * @param fileName   文件名称
     * @param bucketName 源存储桶
     * @return String
     */
    String fileLink(String fileName, String bucketName);

    /**
     * 获取默认桶链接
     */
    String fileLink(String fileName);

    /**
     * 上传文件
     *
     * @param bucketName   存储桶名称
     * @param originalName 上传文件名
     * @param file         文件
     * @param module       模块
     * @return BaseFile
     */
    BaseFile putFile(String bucketName, String module, String originalName, MultipartFile file);


    /**
     * 上传文件
     *
     * @param bucketName   存储桶名称
     * @param originalName 上传文件名
     * @param inputStream  文件
     * @param module       模块
     * @return BaseFile
     */
    BaseFile putFile(String bucketName, String module, String originalName, InputStream inputStream);

    /**
     * 上传文件
     *
     * @param bucketName   存储桶名称
     * @param stream       上传文件InputStream
     * @param originalName 上传文件名
     * @param cover        是否覆盖
     * @param module       模块
     * @return BaseFile
     */
    BaseFile put(String bucketName, String module, InputStream stream, String originalName, boolean cover);

    /**
     * 删除文件
     *
     * @param fileName   文件名称
     * @param bucketName 存储桶名称
     */
    void removeFile(String bucketName, String fileName);


    /**
     * 根据规则生成文件名称规则
     * <p>
     * 文件名称规则: 模块名称/日期-雪花id-原文件名
     *
     * @param originalFilename 原始文件名
     * @param module           模块
     * @return string
     */
    default String getFileName(String module, String originalFilename) {
        // 日期-雪花id-原文件名
        String today = DateUtil.format(new Date(), "yyyy/MM/dd/");
        String uniqueFilename = today + StringPool.SLASH + DateUtil.today() + IdGenerator.getId() + StringPool.DASH + originalFilename;
        if (ObjectUtil.isEmpty(module)) {
            return uniqueFilename;
        }
        if (module.startsWith(StringPool.SLASH)) {
            // 去掉开头的 '/'
            module = module.substring(1);
        }
        if (!module.endsWith(StringPool.SLASH)) {
            // 末尾添加 '/'
            module = module + StringPool.SLASH;
        }
        // 拼接模块名称
        return module + uniqueFilename;
    }

    /**
     * 获取文件流
     *
     * @param bucketName 存储桶
     * @param fileName   文件名称
     * @return InputStream
     */
    InputStream getObject(String bucketName, String fileName);

    /**
     * 根据桶名获取文件列表信息
     *
     * @param bucketName 桶名
     * @return 桶下文件集
     */
    List<BaseFile> listFiles(String bucketName);

    /**
     * 创建分段上传
     *
     * @param bucketName 桶
     * @param filename   文件名
     * @return 分段上传ID
     */
    String createMultipartUpload(String bucketName, String filename);

    /**
     * 分段上传文件
     *
     * @param bytes      文件
     * @param bucketName 桶
     * @param partNumber 分片序号
     * @param filename   文件名
     * @param uploadId   分片上传凭证
     * @return 分片etag
     */
    Etag uploadPart(byte[] bytes, String bucketName, Integer partNumber, String filename, String uploadId);

    /**
     * 完成分段上传
     *
     * @param bucketName 桶
     * @param filename   文件名
     * @param uploadId   凭证
     * @param etagList   分片etag
     */
    void completeMultipartUpload(String bucketName, String filename, String uploadId, Set<Etag> etagList);

    /**
     * 终止分段上传
     *
     * @param bucketName 桶
     * @param filename   文件名
     * @param uploadId   凭证
     */
    void abortMultipartUpload(String bucketName, String filename, String uploadId);

    /**
     * 推送文件到默认桶下面。默认为项目名的桶
     *
     * @param originalName 文件名
     * @param inputStream  文件内容的输入流
     * @param catalogue    文件目录
     * @return 文件上传后的信息
     */
    BaseFile putFile(String originalName, InputStream inputStream, String... catalogue);

    BaseFile putFile(String bucketName, String originalName, InputStream inputStream, String... catalogue);

    /**
     * 推送文本到出桶中
     *
     * @param originalName 文件名
     * @param content      文件内容
     * @param catalogue    文件目录
     * @return 文件上传后的信息
     */
    BaseFile putContent(String originalName, String content, String... catalogue);

}
