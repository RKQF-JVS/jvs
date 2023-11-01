package cn.bctools.oss.utils;

import cn.hutool.core.img.ImgUtil;
import cn.bctools.oss.cons.SystemCons;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: ZhuXiaoKang
 * @Description: 缩略图工具类
 */
@Slf4j
public class ThumbnailUtil {
    /**
     * 默认缩略图文件夹路径
     */
    public static final String RESOURCE_PATH = "META-INF/thumbnail/";

    /**
     * 缩略图base64缓存
     */
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    /**
     * 缩略图枚举
     */
    private enum FileTypeEnum {
        /**
         * name：默认缩略图文件名，与”/resource/META-INF/thumbnail/“文件夹下的文件名相同
         * fileTypes：默认缩略图对应的文件类型（文件类型与FilePathUtils中的类型相同）
         */
        AI("ai", Arrays.asList("ai")),
        EXCEL("excel", Arrays.asList("xls","xlsx")),
        GIF("gif", Arrays.asList("gif")),
        HTML("html", Arrays.asList("html")),
        PDF("pdf", Arrays.asList("pdf")),
        PPT("ppt", Arrays.asList("ppt")),
        PSD("psd", Arrays.asList("psd")),
        SVG("svg", Arrays.asList("svg")),
        TXT("txt", Arrays.asList("txt")),
        WORD("word", Arrays.asList("doc","docx")),
        COMPRESSION("压缩", Arrays.asList("zip","rar","rar4")),
        IMAGE("图片", Arrays.asList("jpeg","jpg","png")),
        FOLDER("文件夹", Arrays.asList("folder")),
        UNKNOWN("未知文件", Arrays.asList("unknown")),
        VIDEO("视频", Arrays.asList("avi","mpeg","mov","rm","swf","flv","mp4")),
        URL("链接", Arrays.asList("url")),
        AUDIO("音频", Arrays.asList("cda","wav","mp3","aif","mid","ape"));

        private String name;
        private List<String> fileTypes;

        FileTypeEnum(String name, List<String> fileTypes) {
            this.name = name;
            this.fileTypes = fileTypes;
        }

        public String getName() {
            return name;
        }

        public List<String> getFileTypes() {
            return fileTypes;
        }

        public static FileTypeEnum getByFileType(String fileType) {
            for(FileTypeEnum currentEnum : FileTypeEnum.values()){
                if(currentEnum.fileTypes.contains(fileType)){
                    return currentEnum;
                }
            }
            return UNKNOWN;
        }

    }

    /**
     * 获取缩略图base64
     * @param fileType
     * @return
     */
    public static String getImageBase64(String fileType) {
        String name = FileTypeEnum.getByFileType(fileType).getName();
        String base64 = CACHE.get(name);
        if (StringUtils.isNotBlank(base64)) {
            return base64;
        }
        // 图片base64加入缓存
        return putCache(name);
    }

    /**
     * 图片转base64并加入缓存
     * @param name 文件名前缀
     * @return
     */
    private static String putCache(String name) {
        String imageFileName = new StringBuilder(RESOURCE_PATH).append(name).append(".png").toString();
        try {
            Enumeration urls = SystemCons.class.getClassLoader().getResources(imageFileName);
            while(urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                Image image = ImgUtil.getImage(url);
                String base64 = ImgUtil.toBase64DataUri(image, "png");
                CACHE.put(name, base64);
                return base64;
            }
        } catch (IOException e) {
            log.error("获取默认缩略图失败。exception:{}", e);
        }
        return null;
    }
}
