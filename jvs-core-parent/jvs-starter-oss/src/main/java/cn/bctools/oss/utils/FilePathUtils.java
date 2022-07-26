package cn.bctools.oss.utils;

import org.springframework.http.MediaType;

/**
 * @author guojing
 */
public class FilePathUtils {

    /**
     * 获取文件的类型，对应的响应类型
     *
     * @param namePath 文件名
     * @author: guojing
     * @return: org.springframework.http.MediaType
     */
    public static MediaType getMediaType(String namePath) {
        int i = namePath.indexOf(".") + 1;
        String substring = namePath.substring(i);
        MediaType mediaType;
        switch (substring) {
            case "jpg":
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
        }
        return mediaType;
    }


    /**
     * 获取文件的类型，对应的响应类型
     *
     * @param namePath 文件名
     * @author: guojing
     * @return: org.springframework.http.MediaType
     */
    public static String getMinorMediaType(String namePath) {
        int i = namePath.indexOf(".") + 1;
        String substring = namePath.substring(i);
        switch (substring) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
        }
        return substring;
    }
}
