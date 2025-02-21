package com.shimh.service.impl;

import com.shimh.service.PdfConversionStrategy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PdfToPngConversionStrategy implements PdfConversionStrategy {
    @Override
    public void convert(String pdfFilePath, String outputDir) throws Exception {
        File pdfFile = new File(pdfFilePath);
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // 遍历每一页并转换为PNG
        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
            File outputFile = new File(outputDir + File.separator + "page_" + (pageIndex + 1) + ".png");
            ImageIO.write(image, "PNG", outputFile);
        }

        document.close();
    }
}
