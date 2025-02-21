package com.shimh.service;

public interface PdfConversionStrategy {
    void convert(String pdfFilePath, String outputDir) throws Exception;
}
