package com.zk.commons.util.utils;

import fr.opensagres.poi.xwpf.converter.core.XWPFConverterException;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import java.io.*;


public class WordToPDF {

    @Test
    public void TestChineseCharacters()
            throws XWPFConverterException, IOException
    {

        OutputStream out = new FileOutputStream( new File( "C:\\Users\\Administrator\\Desktop\\test.pdf" ) );//testitext.pdf

        XWPFDocument document =
                new XWPFDocument(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\1.0.1.docx")));

        PdfOptions options = PdfOptions.create();

        PdfConverter.getInstance().convert( document, out, options );
    }
}