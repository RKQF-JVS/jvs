package cn.bctools.oss.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.common.utils.R;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.po.OssFile;

import java.util.List;

/**
 * 文件上传和下载读取的数据存储
 *
 * @author Administrator
 */
public interface FileDataInterface {

    /**
     * 保存一个文件
     *
     * @param baseFile
     */
    void insert(BaseFile baseFile);

    /**
     * 创建一个新的桶
     */
    void newBucket(String bucketName);

    /**
     * 分页查询数据
     */
    Page page(Page<OssFile> page, String fileName, String fileType, Long startTime, Long endTime, String label);

    /**
     * 获取桶名
     */
    List<String> buckets();

    /**
     * 获取文件类型集合
     *
     * @param bucketName 指定桶
     * @return 文件类型集
     */
    List<String> fileTypes(String bucketName);

    /**
     * 根据文件类型获取所有标签
     */
    List<String> fileLabel(String fileLabel);
}
