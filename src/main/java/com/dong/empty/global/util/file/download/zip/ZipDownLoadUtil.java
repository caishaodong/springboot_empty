package com.dong.empty.global.util.file.download.zip;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author caishaodong
 * @Date 2020-10-16 18:03
 * @Description
 **/
public class ZipDownLoadUtil {
    public static void download(HttpServletRequest request, HttpServletResponse response) {
        String downloadName = "下载文件名称.zip";
        // 下载文件名乱码问题解决
//        downloadName = BrowserCharCodeUtils.browserCharCodeFun(request, downloadName);

        // 将文件进行打包下载
        try {
            OutputStream out = response.getOutputStream();
            // 服务器存储地址
            byte[] data = createZip("/fileStorage/download");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;fileName=" + downloadName);
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream;charset=UTF-8");
            IOUtils.write(data, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] createZip(String srcSource) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        // 将目标文件打包成zip导出
        File file = new File(srcSource);
        a(zip, file, "");
        zip.close();
        return outputStream.toByteArray();
    }

    public static void a(ZipOutputStream zip, File file, String dir) throws Exception {
        // 如果当前的是文件夹，则进行进一步处理
        if (file.isDirectory()) {
            // 得到文件列表信息
            File[] files = file.listFiles();
            // 将文件夹添加到下一级打包目录
            zip.putNextEntry(new ZipEntry(dir + "/"));
            dir = dir.length() == 0 ? "" : dir + "/";
            // 循环将文件夹中的文件打包
            for (int i = 0; i < files.length; i++) {
                //递归处理
                a(zip, files[i], dir + files[i].getName());
            }
        } else {
            // 当前的是文件，打包处理
            // 文件输入流
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(dir);
            zip.putNextEntry(entry);
            zip.write(FileUtils.readFileToByteArray(file));
            bis.close();
            zip.flush();
            zip.closeEntry();
        }
    }
}
