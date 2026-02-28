package org.escolar.controller;

import com.itextpdf.kernel.pdf.PdfDocument;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocxToPdfConverter {

    public void convertDocxToPdf(String inputDocxPath, String outputPdfPath) throws IOException {
        // Carregar o arquivo DOCX
        FileInputStream docxInputStream = new FileInputStream(inputDocxPath);
        XWPFDocument docxDocument = new XWPFDocument(docxInputStream);

        // Criar um documento PDF com iText
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPdfPath));
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Ler o conteúdo do DOCX e adicionar ao PDF
       
        for(XWPFParagraph paragraph : docxDocument.getParagraphs()) {
        	 String text = paragraph.getText();
             document.add(new Paragraph(text));
        }
//        
//        docxDocument.getParagraphs().forEach(paragraph -> {
//        	 String text = paragraph.getText();
//             document.add(new Paragraph(text));
//        });

        // Fechar documentos
        document.close();
        pdfDocument.close();
        docxDocument.close();
        docxInputStream.close();
    }
    
    public byte[] convertPDFToBytes(String pdfFilePath) throws IOException {
        Path path = Paths.get(pdfFilePath);
        return Files.readAllBytes(path);
    }
}
