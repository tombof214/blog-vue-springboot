package com.shimh.service.impl;

import com.shimh.service.ConversionStrategy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PdfToJpgConversionStrategy implements ConversionStrategy {
    @Override
    public void convert(String pdfFilePath, String outputDir) throws Exception {
        File pdfFile = new File(pdfFilePath);
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // 遍历每一页并转换为JPG
        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
            File outputFile = new File(outputDir + File.separator + "page_" + (pageIndex + 1) + ".jpg");
            ImageIO.write(image, "JPEG", outputFile);
        }

        document.close();
    }
}
