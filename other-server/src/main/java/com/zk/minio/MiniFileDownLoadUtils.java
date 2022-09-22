package com.zk.minio;

import com.alibaba.excel.EasyExcel;
import com.zk.commons.exception.ServiceException;
import com.zk.minio.excelread.ImportDataExcelReadListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 通过url取得文件返回InputStream类型数据
 *
 */
@Slf4j
public class MiniFileDownLoadUtils {

    private static final Path TMPDIR = Path.of(System.getProperty("java.io.tmpdir"));

    private MiniFileDownLoadUtils() {
    }

    /**
     * @param ossUrlPrefix oss服务地址前缀 安全需要
     * @return
     * @throws new ServiceException("不合法的文件地址");
     * @description: 从服务器获得一个输入流(本例是指从服务器获得一个image输入流)
     * @date: 2019年12月7日
     */
    public static InputStream getInputStream(String urlPath, String ossUrlPrefix) {
        if (!urlPath.startsWith(ossUrlPrefix)) {
            throw new ServiceException("不合法的文件地址");
        }
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlPath);
            log.info("读取文件urlPath：---------------------------{}", url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(120000);
            httpURLConnection.setReadTimeout(120000);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            //可设置请求头
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            log.debug("responseCode is:" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 从服务器返回一个输入流
                inputStream = httpURLConnection.getInputStream();
                log.info("读取文件成功：{}", responseCode);
            } else {
                log.info("读取文件失败：{}", responseCode);
                inputStream = httpURLConnection.getErrorStream();
            }
        } catch (IOException e) {
            log.error("读取文件失败：------", e);
        }
        return inputStream;
    }

    /**
     * 将InputStream写入本地文件
     *
     * @param path
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException IOException
     */
    private static File writeToLocal(String path, String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        File file = new File(path, destination);
        FileOutputStream downloadFile = null;
        try {
            boolean isCreated = file.createNewFile(); // 创建文件
            log.info("创建本地临时文件：{}，path：{}，destination：{}", isCreated, path, destination);

            if (isCreated) {
                downloadFile = new FileOutputStream(file);
                while ((index = input.read(bytes)) != -1) {
                    downloadFile.write(bytes, 0, index);
                    downloadFile.flush();
                }

                downloadFile.close();
                return file;
            }
            return null;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("将数据文件，写入到本地，创建临时文件失败");
        } finally {
            try {
                // 关闭资源
                if (Objects.nonNull(input)) {
                    input.close();
                }
                if (Objects.nonNull(downloadFile)) {
                    downloadFile.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 从txt或者excel中读取行数据
     *
     * @param urlPath
     * @param isExcel
     * @param ossUrlPrefix
     * @return List<String> 读取到的行数据
     */
    public static List<String> getDataFromUrl(String urlPath, boolean isExcel, String ossUrlPrefix) {

        // 校验：文件
        if (StringUtils.isEmpty(urlPath)) {
            throw new ServiceException("请提供导入数据的文件");
        }

        InputStream stream = MiniFileDownLoadUtils.getInputStream(urlPath, ossUrlPrefix);
        File file = null;
        try {
            String fileName = UUID.randomUUID() + ".txt";
            if (isExcel) {
                fileName = UUID.randomUUID().toString();
            }
            file = MiniFileDownLoadUtils.writeToLocal(TMPDIR.toString(), fileName, stream);

        } catch (IOException e1) {
            log.error("urlPath -----------------------------{}", urlPath);
            throw new ServiceException("将数据文件，写入到本地，创建临时文件失败");
        } finally {
            try {
                if (Objects.nonNull(file)) {
                    log.error("文件已经被复制到临时文件路径： {}", file.getPath());
                    // 删除使用过的临时文件
                    if (file.exists()) {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        try {
            List<String> string = null;
            if (!isExcel) {
                // 如果是txt文件
                String fileName = file.getName().substring(0, file.getName().indexOf("."));
                Path path = generateTempPath(fileName, ".txt");
                // 根据临时文件读取数据
                string = Files.readAllLines(path);
            } else {
                // 如果是excel文件
//                ExcelReader reader = ExcelUtil.getReader(file);
//                List<Map<String, Object>> readAll = reader.readAll();
                // 这里 需要指定读用哪个listener去读，然后读取第一个sheet；listener中saveData();可以避免readAll造成的内存溢出 文件流会自动关闭
                string = new ArrayList<>();
                ImportDataExcelReadListener listener = new ImportDataExcelReadListener(string);
                EasyExcel.read(file, listener).sheet().doRead();
//                string = readAll.stream().map(t -> JSONUtil.toJsonStr(t)).collect(Collectors.toList());
            }

            return string;

        } catch (Exception e) {
            log.error("读取数据文件失败", e);
            throw new ServiceException("读取数据文件失败！");
        } finally {
            try {
                // 删除使用过的临时文件
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 生成系统临时文件目录
     *
     * @param fileName
     * @param suffix
     * @return
     */
    private static Path generateTempPath(String fileName, String suffix) {
        return Path.of(TMPDIR + File.separator + fileName + suffix);
    }
}
