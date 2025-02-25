package com.shimh.service;

public interface ConversionStrategy {
    void  convert(String pdfFilePath, String outputDir) throws Exception;
}
