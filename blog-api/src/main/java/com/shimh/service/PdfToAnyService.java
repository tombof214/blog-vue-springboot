package com.shimh.service;

import org.springframework.stereotype.Service;

@Service
public class PdfToAnyService {

    private PdfConversionStrategy conversionStrategy;

    public void setConversionStrategy(PdfConversionStrategy conversionStrategy) {
        this.conversionStrategy = conversionStrategy;
    }

    public void convert(String pdfFilePath, String outputDir) throws Exception {
        if (conversionStrategy == null) {
            throw new IllegalStateException("Conversion strategy not set");
        }
        conversionStrategy.convert(pdfFilePath, outputDir);
    }
}
