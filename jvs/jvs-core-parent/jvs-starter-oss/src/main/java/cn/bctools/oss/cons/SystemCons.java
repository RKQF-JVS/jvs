package cn.bctools.oss.cons;

/**
 * @author 
 */
public class SystemCons {
    /**
     * 大文件上传标识
     */
    public final static String R_KEY = "file:bigfile:certificate:%s";

    public static final int MIN_MULTIPART_SIZE = 5 * 1024 * 1024;
    /**
     * 富文本编辑框上传图片使用
     */
    protected static String EDITOR = "\n" +
            "{\n" +
            "    \"imageActionName\": \"uploadimage\", \n" +
            "    \"imageFieldName\": \"upfile\", \n" +
            "    \"imageMaxSize\": 2048000, \n" +
            "    \"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +
            "    \"imageCompressEnable\": true, \n" +
            "    \"imageCompressBorder\": 1600, \n" +
            "    \"imageInsertAlign\": \"none\", \n" +
            "    \"imageUrlPrefix\": \"\", \n" +
            "    \"imagePathFormat\": \"/img/{time}{rand:2}\", \n" +
            "    \"scrawlActionName\": \"uploadscrawl\", \n" +
            "    \"scrawlFieldName\": \"upfile\", \n" +
            "    \"scrawlPathFormat\": \"img/{time}{rand:2}\", \n" +
            "    \"scrawlMaxSize\": 2048000, \n" +
            "    \"scrawlUrlPrefix\": \"\", \n" +
            "    \"scrawlInsertAlign\": \"none\",\n" +
            "    \"snapscreenActionName\": \"uploadimage\", \n" +
            "    \"snapscreenPathFormat\": \"img/{time}{rand:2}\", \n" +
            "    \"snapscreenUrlPrefix\": \"\", \n" +
            "    \"snapscreenInsertAlign\": \"none\", \n" +
            "    \"catcherLocalDomain\": [\"127.0.0.1\", \"localhost\", \"img.dcssn.com\"],\n" +
            "    \"catcherActionName\": \"catchimage\", \n" +
            "    \"catcherFieldName\": \"source\", \n" +
            "    \"catcherPathFormat\": \"img/{time}{rand:2}\", \n" +
            "    \"catcherUrlPrefix\": \"\", \n" +
            "    \"catcherMaxSize\": 2048000, \n" +
            "    \"catcherAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +
            "    \"videoActionName\": \"uploadvideo\", \n" +
            "    \"videoFieldName\": \"upfile\", \n" +
            "    \"videoPathFormat\": \"video/{time}{rand:2}\", \n" +
            "    \"videoUrlPrefix\": \"\", \n" +
            "    \"videoMaxSize\": 102400000, \n" +
            "    \"videoAllowFiles\": [\n" +
            "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
            "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"], \n" +
            "    \"fileActionName\": \"uploadfile\", \n" +
            "    \"fileFieldName\": \"upfile\", \n" +
            "    \"filePathFormat\": \"file/{time}{rand:2}\", \n" +
            "    \"fileUrlPrefix\": \"\", \n" +
            "    \"fileMaxSize\": 51200000, \n" +
            "    \"fileAllowFiles\": [\n" +
            "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
            "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
            "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
            "        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
            "        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
            "    ], \n" +
            "    \"imageManagerActionName\": \"listimage\", \n" +
            "    \"imageManagerListPath\": \"img/\", \n" +
            "    \"imageManagerListSize\": 20, \n" +
            "    \"imageManagerUrlPrefix\": \"\", \n" +
            "    \"imageManagerInsertAlign\": \"none\", \n" +
            "    \"imageManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +
            "    \"fileManagerActionName\": \"listfile\", \n" +
            "    \"fileManagerListPath\": \"file/\", \n" +
            "    \"fileManagerUrlPrefix\": \"\", \n" +
            "    \"fileManagerListSize\": 20, \n" +
            "    \"fileManagerAllowFiles\": [\n" +
            "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
            "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
            "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
            "        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
            "        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
            "    ] \n" +
            "}\n";

    public static String getEditor() {
        return EDITOR;
    }
}
