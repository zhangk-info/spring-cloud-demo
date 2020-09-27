package com.zk.utils.tika;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class TikaUtils {

    public static void main(String[] args) throws Exception {
//        String path = "C:\\Users\\Administrator\\Desktop\\备忘\\wps.pdf";
//        String text = TikaUtil.parseToString(path);
//        log.error(text);

        String url = "https://";
        URL u = new URL(url);
        String text = TikaUtils.parseToString(u);
        log.error(text);
    }

    /**
     * 获取文件信息
     */
    public static Metadata getMetadata(String path) {
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        Tika tika = new Tika(tikaConfig);
        Parser parser = tika.getParser();

        Metadata metadata = new Metadata();
        try {
            try (InputStream stream = new FileInputStream(new File(path))) {
                parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
            }
            log.info("Simple Excel document", metadata.get(TikaCoreProperties.TITLE));
        } catch (Exception e) {
            log.debug("TikaUtil.parse throw Exception" + e);
        }
        return metadata;
    }

    /**
     * 将文件转换为文本
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String parseToString(String path) throws Exception {
        TikaConfig tc = TikaConfig.getDefaultConfig();
        Tika tika = new Tika(tc);
        System.out.println(path);
        System.out.println(tika.detect(new File(path)));
        String text = tika.parseToString(new File(path));
        return text;
    }

    /**
     * 将文件转换为文本
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String parseToString(URL url) throws Exception {

        TikaConfig tc = TikaConfig.getDefaultConfig();
        Tika tika = new Tika(tc);
        System.out.println(url);
        System.out.println(tika.detect(url));
        String text = tika.parseToString(url);
        return text;
    }
}