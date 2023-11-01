package cn.bctools.gateway.utils;

import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.SpringContextUtil;

/**
 * 默认响应加密方式
 *
 * @author Administrator
 */
public class DataUtil {

    private DataUtil() {
    }

    /**
     * 加密
     *
     * @param body
     * @return
     */
    public static String encodeBody(byte[] body) {
        // 服务端私钥
        //用使用base64进行加密和解密
        return PasswordUtil.encodePassword(new String(body), SpringContextUtil.getKey());
    }

    /**
     * 解密
     *
     * @param body
     * @return
     */
    public static String decodeBody(byte[] body) {
        return PasswordUtil.decodedPassword(new String(body), SpringContextUtil.getKey());
    }

}
