package cn.bctools.oss.controller;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.bctools.log.annotation.Log;
import cn.bctools.oss.component.FileUploadComponent;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.dto.Etag;
import cn.bctools.oss.dto.FileLinkDto;
import cn.bctools.oss.dto.FileNameDto;
import cn.bctools.oss.po.OssFile;
import cn.bctools.oss.service.FileDataInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 〈文件上传管理〉
 *
 * @since: 1.0.0
 * @author: auto
 */
@Slf4j
@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "文件上传下载")
public class FileUploadController {

    FileUploadComponent fileUploadComponent;
    FileDataInterface fileDataInterface;

    @Log
    @ApiOperation("文件列表")
    @GetMapping("/file/list")
    public R<Page<OssFile>> list(Page<OssFile> page,
                                 @RequestParam(value = "fileType", required = false) String fileType,
                                 @RequestParam(value = "startTime", required = false) Long startTime,
                                 @RequestParam(value = "endTime", required = false) Long endTime,
                                 @RequestParam(value = "label", required = false) String label,
                                 @RequestParam(value = "bucketName", required = false) String bucketName,
                                 @RequestParam(value = "fileName", required = false) String fileName) {
        fileDataInterface.page(page, fileName, fileType, startTime, endTime, bucketName, label);
        return R.ok(page);
    }


    @Log
    @ApiOperation("获取桶")
    @GetMapping("/buckets")
    public R<List<String>> buckets() {
        List<String> buckets = fileDataInterface.buckets();
        return R.ok(buckets);
    }

    @Log
    @ApiOperation("根据文件类型获取标签集")
    @GetMapping("/fileLabel")
    public R<List<String>> fileLabel(@RequestParam(value = "fileLabel", required = false) String fileLabel) {
        List<String> strings = fileDataInterface.fileLabel(fileLabel);
        return R.ok(strings);
    }

    @Log
    @ApiOperation("获取文件类型集")
    @GetMapping("/fileTypes")
    public R<List<String>> fileTypes(@RequestParam(value = "bucketName", required = false) String bucketName) {
        List<String> strings = fileDataInterface.fileTypes(bucketName);
        return R.ok(strings);
    }


    /**
     * 〈文件上传〉
     *
     * @param file       文件对象
     * @param bucketName 文件桶名
     * @param module     模块对象，为不同服务模块区分不同的服务
     * @return BaseFile 文件基本信息
     * @throws BusinessException 文件上传异常
     * @since: 1.0.0
     * @author: auto
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload/{bucketName}")
    public R<FileNameDto> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "module", required = false) String module, @PathVariable String bucketName, @RequestParam(value = "label", defaultValue = "默认") String label) throws BusinessException {
        SystemThreadLocal.set("label", label);
        FileNameDto target = new FileNameDto();
        BaseFile source = fileUploadComponent.doUpload(file, module, bucketName);
        BeanUtil.copyProperties(source, target, true);
        target.setOriginalFileName(file.getOriginalFilename());
        target.setFileLink(fileUploadComponent.fileLink(target.getBucketName(), target.getFileName()));
        target.setFileSize(source.getSize());
        return R.ok(target);
    }

    /**
     * 〈上传base64〉
     *
     * @param file       base64
     * @param bucketName 文件桶名
     * @param module     模块对象，为不同服务模块区分不同的服务
     * @return BaseFile 文件基本信息
     * @throws BusinessException 文件上传异常
     * @since: 1.0.0
     * @author: auto
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload/base64/{bucketName}")
    public R<FileNameDto> upload(@RequestParam("file") String file, @RequestParam(value = "module", required = false) String module, @PathVariable String bucketName, @RequestParam(value = "label", defaultValue = "默认") String label) throws BusinessException {
        SystemThreadLocal.set("label", label);
        FileNameDto target = new FileNameDto();
        BaseFile source = fileUploadComponent.doUpload(file, module, bucketName);
        BeanUtil.copyProperties(source, target, true);
        target.setOriginalFileName(source.getFileName());
        target.setFileLink(fileUploadComponent.fileLink(target.getBucketName(), target.getFileName()));
        target.setFileSize(source.getSize());
        return R.ok(target);
    }

    /**
     * 〈文件外链〉
     *
     * @param bucketName 桶名称
     * @return 文件外链
     * @since: 1.0.0
     * @author: auto
     */
    @ApiOperation("预览文件")
    @GetMapping("/file/link/{bucketName}")
    public R<String> fileLink(@PathVariable("bucketName") String bucketName, @RequestParam("fileName") String fileName) {
        String fileLink = fileUploadComponent.fileLink(bucketName, fileName);
        return R.ok(fileLink);
    }

    @ApiOperation("获取文件集合的文件地址")
    @PostMapping("/file/links")
    public R<List<FileLinkDto>> filelinks(@RequestBody List<FileLinkDto> list) {
        list.parallelStream().filter(f0 -> StrUtil.isAllNotBlank(f0.getBucketName(), f0.getFileName()))
                .forEach(m -> {
                    try {
                        m.setFileLink(fileUploadComponent.fileLink(m.getBucketName(), m.getFileName()));
                    } catch (Exception ignored) {
                    }
                });
        return R.ok(list);
    }

    @SneakyThrows
    @ApiOperation("打开下载文件")
    @GetMapping("/bytes/{bucketName}")
    public ResponseEntity<byte[]> bytes(@PathVariable("bucketName") String bucketName, @RequestParam("fileName") String fileName) {
        InputStream object = fileUploadComponent.getObject(bucketName, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(URLEncoder.encode(fileName, "UTF-8")))
                .body(IoUtil.readBytes(object));
    }

    /**
     * 获取大文件上传凭证
     *
     * @param bucketName      桶
     * @param filename        文件名
     * @param totalPartNumber 分片数量：大文件切分的数量
     * @return 凭证
     */
    @ApiOperation(value = "获取大文件上传凭证", notes = "需要对大文件按5M进行严格切割，只能最后一个切片的大小可以不为5M，其余切片必须为5M，且对切片进行按顺序进行标号。凭证有时效性，为3小时。")
    @GetMapping("/largeFile/uploadCertificate/{bucketName}/{filename}/{totalPartNumber}")
    public R<Etag> getLargeFileUploadCertificate(@PathVariable("bucketName") String bucketName, @PathVariable("filename") String filename, @PathVariable("totalPartNumber") Integer totalPartNumber) {
        return R.ok(fileUploadComponent.createMultipartUpload(bucketName, filename, totalPartNumber));
    }

    /**
     * 上传片段
     *
     * @param file       文件
     * @param bucketName 桶
     * @param uploadId   凭证
     * @param partNumber 分片序号
     * @return R
     */
    @SneakyThrows
    @ApiOperation("分片上传")
    @PostMapping(value = "/uploadPart/{bucketName}/{uploadId}/{partNumber}")
    public R<String> uploadPart(@RequestHeader(value = "extra", required = false) String extra, @RequestParam("file") MultipartFile file, @PathVariable("bucketName") String bucketName, @PathVariable("uploadId") String uploadId, @PathVariable("partNumber") Integer partNumber) {
        byte[] bytes = IoUtil.readBytes(file.getInputStream());
        String fileLink = fileUploadComponent.uploadPart(bytes, bucketName, partNumber, uploadId);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            if (response != null) {
                response.addHeader("extra", extra);
            }
        }
        return R.ok(fileLink);
    }

}
