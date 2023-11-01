package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.bctools.auth.entity.enums.BulletinPublishEnum;
import cn.bctools.database.entity.po.BasalPo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 系统应用公告
 * @author zhuxiaokang
 * @since 2021-11-19
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_bulletin", autoResultMap = true)
public class Bulletin extends BasalPo implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 系统应用APPID数组
     */
    @TableField(value = "app_keys", typeHandler = JacksonTypeHandler.class)
    private List<String> appKeys;

    /**
     * 公告生效时间
     */
    @TableField("start_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 公告结束时间
     */
    @TableField("end_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 是否发布 0-待发布, 1-发布
     */
    private Boolean publish;

    /**
     * 是否删除  -1：已删除  0：正常
     */
    @TableField("del_flag")
    @TableLogic
    private Boolean delFlag;


}
