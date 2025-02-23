package com.shimh.controller;

import com.shimh.service.PdfToAnyService;
import com.shimh.service.impl.PdfToJpgConversionStrategy;
import com.shimh.service.impl.PdfToPngConversionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfToAnyService pdfToAnyService;

    // 文件上传接口，支持多个文件上传
    @PostMapping("/upload")
    public Object uploadPdf(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0) {
            return new ErrorResponse("没有选择任何文件");
        }

        try {
            // 设置文件保存的路径为 D:/uploads/
            String uploadDirectory = "D:/uploads/";
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();  // 如果目录不存在，则创建
                if (!created) {
                    return new ErrorResponse("创建目录失败");
                }
            }

            StringBuilder filePaths = new StringBuilder();
            // 处理每个文件并保存
            for (MultipartFile file : files) {
                String filePath = uploadDirectory + file.getOriginalFilename();
                File serverFile = new File(filePath);
                file.transferTo(serverFile);
                filePaths.append(filePath).append("; ");
            }

            // 返回上传文件路径
            return new SuccessResponse(filePaths.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse("文件上传失败: " + e.getMessage());
        }
    }

    // 文件转换接口，接收多个文件路径并进行批量转换
    @PostMapping("/convert")
    public String convertPdf(@RequestParam("pdfFiles") MultipartFile[] pdfFiles,
                             @RequestParam("outputDir") String outputDir,
                             @RequestParam("format") String format) {
        try {
            if (pdfFiles.length == 0) {
                return "错误: 没有文件上传";
            }
            if (outputDir == null || outputDir.isEmpty()) {
                return "错误: 输出目录不能为空";
            }
            if (format == null || format.isEmpty()) {
                return "错误: 转换格式不能为空";
            }

            // 设置转换策略
            switch (format.toLowerCase()) {
                case "jpg":
                    pdfToAnyService.setConversionStrategy(new PdfToJpgConversionStrategy());
                    break;
                case "png":
                    pdfToAnyService.setConversionStrategy(new PdfToPngConversionStrategy());
                    break;
                default:
                    return "不支持的格式: " + format;
            }

            // 执行批量转换
            for (MultipartFile file : pdfFiles) {
                // 对每个文件执行转换
                String pdfFilePath = "D:/uploads/" + file.getOriginalFilename(); // 假设文件已保存
                pdfToAnyService.convert(pdfFilePath, outputDir);
            }

            return "PDF转换成功，输出目录: " + outputDir;

        } catch (Exception e) {
            e.printStackTrace();
            return "转换失败: " + e.getMessage();
        }
    }

    // 用于成功响应的类
    static class SuccessResponse {
        private String filePaths;

        public SuccessResponse(String filePaths) {
            this.filePaths = filePaths;
        }

        public String getFilePaths() {
            return filePaths;
        }

        public void setFilePaths(String filePaths) {
            this.filePaths = filePaths;
        }
    }

    // 用于错误响应的类
    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
