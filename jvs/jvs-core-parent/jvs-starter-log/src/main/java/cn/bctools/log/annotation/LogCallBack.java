package cn.bctools.log.annotation;

/**
 * @author 
 */
public interface LogCallBack {

    /**
     * 回调事件
     */
    void callBack(String createBy, String description, String content);

}
