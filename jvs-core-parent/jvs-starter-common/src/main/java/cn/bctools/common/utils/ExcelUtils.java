package cn.bctools.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author guojing
 */
@Slf4j
public class ExcelUtils {

    /**
     * 将excel直接读成List<T>
     *
     * @param clazz 带有EasyExcel注解的类,注意 不要带 @Accessor 注解
     * @param <T>
     * @return
     * @throws IOException
     */
    @SneakyThrows
    private static <T> List<T> readAll(InputStream inputStream, Class<T> clazz) {
        List<T> data = new ArrayList<>();
        try {
            data = EasyExcelFactory.read(inputStream).head(clazz).doReadAllSync();
        } catch (Exception e) {
            log.info("解析excel文件异常,异常原因:{}", e.getMessage());
            throw new Exception("请使用标准的excel文件模板上传");
        }
        if (data.isEmpty()) {
            throw new Exception("未读取到模板内容,导入未执行");
        }
        return data;
    }

    @SneakyThrows
    public static <T> List<T> readAll(MultipartFile file, Class<T> clazz) {
        return readAll(file.getInputStream(), clazz);
    }

    /**
     * Excel数据导入，分段导入,最大3千个保存一次
     * 3千一次默认推荐使用saveBatch用做保存，是否开启事务处理
     *
     * @param inputStream 文件流
     * @param cls         文字的Class
     * @param service     保存的service
     * @param <T>         入参的值
     */
    public static <T> void importExcel(InputStream inputStream, Class<T> cls, Consumer<List<T>> service) {
        EasyExcel.read(inputStream, cls, new AnalysisEventListener() {
            private static final int BATCH_COUNT = 3000;
            List list = new ArrayList();

            @Override
            public void invoke(Object data, AnalysisContext context) {
                list.add(data);
                if (list.size() >= BATCH_COUNT) {
                    service.accept(list);
                    list.clear();
                }
            }

            @Async
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                service.accept(list);
                log.info("所有数据解析完成！");
            }
        }).sheet().doRead();
    }

    /**
     * Excel导出功能
     * <p>
     * //        ServletOutputStream outputStream = response.getOutputStream();
     * //        response.setCharacterEncoding("utf-8");
     * //        response.setHeader(Header.CONTENT_DISPOSITION.toString(), "attachment; filename=".concat(URLEncoder.encode(fileName, "UTF-8")));
     * //        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
     *
     * @param fileName  文件名
     * @param sheetName sheet名
     * @param cls       对象属性名的值
     * @param <T>
     */
    @SneakyThrows
    public static <T> void export(OutputStream outputStream, String fileName, String sheetName, Class<T> cls, List<T> list) {
        EasyExcel.write(outputStream, cls).sheet(sheetName).doWrite(list);
    }
}
