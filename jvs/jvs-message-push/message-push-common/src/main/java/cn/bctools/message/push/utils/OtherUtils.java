package cn.bctools.message.push.utils;

import java.util.UUID;

public class OtherUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
