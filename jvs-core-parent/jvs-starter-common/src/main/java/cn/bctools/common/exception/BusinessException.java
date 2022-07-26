package cn.bctools.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author guojing
 * @describe 运行业务运行时异常
 */
@Data
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -7517200941745261512L;

    /**
     * 业务错误code
     */
    private int code = -1;

    /**
     * 内部使用的异常处理
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
    }


    /**
     * 内部使用的异常处理,增强,适用于message需要拼接多个参数
     *
     * @param message 错误信息
     * @param params  参数列表
     */
    public BusinessException(String message, Object... params) {
        super(StrUtil.format(message,params));
    }

    /**
     * 外部使用，如果出现了外部使用的异常必须使用Code
     *
     * @param message 错误信息
     * @param code    错误code、不同的系统，可以使用不同的code
     */
    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * @param message 错误信息
     * @param code    错误code、不同的系统，可以使用不同的code、系统的异常数据
     */
    public BusinessException(String message, int code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
