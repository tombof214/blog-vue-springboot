package com.shimh.controller;

import com.shimh.service.PdfToAnyService;
import com.shimh.service.impl.PdfToJpgConversionStrategy;
import com.shimh.service.impl.PdfToPngConversionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfToAnyService pdfToAnyService;

    // 文件上传接口，上传后返回文件路径
    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空，上传失败";
        }

        try {
            // 设置文件保存的路径为 D:/uploads/
            String uploadDirectory = "D:/uploads/";
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();  // 如果目录不存在，则创建
                if (!created) {
                    return "创建目录失败";
                }
            }

            // 获取文件保存的路径（包括文件名）
            String filePath = uploadDirectory + file.getOriginalFilename();
            System.out.println("生成的保存路径: " + filePath);  // 打印生成的保存路径

            // 打印绝对路径
            File serverFile = new File(filePath);
            System.out.println("服务器保存路径（绝对路径）: " + serverFile.getAbsolutePath());

            // 保存文件
            file.transferTo(serverFile);
            System.out.println("qwq");
            // 直接返回文件路径
            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败: " + e.getMessage();
        }
    }
    // 文件转换接口，接收文件路径并进行转换
    @PostMapping("/convert")
    public String convertPdf(@RequestParam("pdfFile") String pdfFilePath,
                             @RequestParam("outputDir") String outputDir,
                             @RequestParam("format") String format) {
        try {
            // 参数验证
            if (pdfFilePath == null || pdfFilePath.isEmpty()) {
                return "错误: PDF文件路径不能为空";
            }
            if (outputDir == null || outputDir.isEmpty()) {
                return "错误: 输出目录不能为空";
            }
            if (format == null || format.isEmpty()) {
                return "错误: 转换格式不能为空";
            }

            // 检查 PDF 文件是否存在
            File pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists()) {
                return "错误: PDF 文件不存在: " + pdfFilePath;
            }
            if (!pdfFile.canRead()) {
                return "错误: 无法读取文件: " + pdfFilePath;
            }

            // 检查并创建输出目录
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                boolean dirsCreated = outputDirFile.mkdirs();  // 创建目录，包括子目录
                if (!dirsCreated) {
                    return "无法创建输出目录: " + outputDir;
                }
            }

            if (!outputDirFile.canWrite()) {
                return "错误: 输出目录没有写入权限: " + outputDir;
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

            // 执行转换
            pdfToAnyService.convert(pdfFilePath, outputDir);
            return "PDF转换成功，输出目录: " + outputDir;
        } catch (Exception e) {
            e.printStackTrace();
            return "转换失败: " + e.getMessage();
        }
    }
}
