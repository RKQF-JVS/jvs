package cn.bctools.oss.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * @author guojing
 * @describe
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_file")
public class OssFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 源文件名称
     */
    private String fileName;
    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 模块名称
     */
    private String module;
    /**
     * 文件全地址
     */
    private String filePath;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 文件大小 处理好的格式
     */
    @TableField(exist = false)
    private String fileSize;
    /**
     * 文件格式
     */
    private String fileType;
    /**
     * 文件连接
     */
    private String fileLink;
    /**
     * 标签  方便快速搜索和查询处理
     */
    private String label;

    /**
     * 缩略图
     */
    @TableField(exist = false)
    private String thumbnail;

    public String getFileSize() {
        return this.size == null ? "" : byteFormat(this.size, true);
    }

    /**
     * @param bytes    转换得字节
     * @param needUnit 是否需要单位
     * @return
     */
    public static String byteFormat(long bytes, boolean needUnit) {
        String[] units = new String[]{" B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB"};
        if (bytes <= 0L) {
            return "0.0 B";
        }
        int unit = 1024;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        double pre = 0;
        if (bytes > 1024) {
            pre = bytes / Math.pow(unit, exp);
        } else {
            pre = (double) bytes / (double) unit;
        }
        if (needUnit) {
            return String.format(Locale.ENGLISH, "%.1f%s", pre, units[(int) exp]);
        }
        return String.format(Locale.ENGLISH, "%.1f", pre);
    }


}
