package com.dong.empty.controller;

import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.base.BaseController;
import com.dong.empty.global.util.date.LocalDateTimeUtil;
import com.dong.empty.global.util.file.download.zip.ZipDownLoadUtil;
import com.dong.empty.global.util.file.excel.ExcelUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author caishaodong
 * @Date 2020-10-16 10:50
 * @Description
 **/
@RestController
@RequestMapping("/excel")
public class ExcelController extends BaseController {

    @GetMapping("/exportByTemplate")
    public ResponseResult exportByTemplate(HttpServletResponse response) {
        String templatePath = System.getProperty("user.dir") + "/src/main/resources/templates/excel/exportTemplate.xlsx";
        Map<String, String> map = getExpList();
        try {
            ExcelUtil.createSheetByTemplate(templatePath, map, response);
        } catch (Exception e) {
            e.printStackTrace();
            return error();
        }
        return success();
    }

    public Map<String, String> getExpList() {
        Map<String, String> map = new HashMap<>();
        map.put("date", LocalDateTimeUtil.formatDateTimeByPattern(LocalDateTime.now(), "yyyy-MM-dd"));
        map.put("sales", "1234");
        map.put("customservice", "客服张三");
        map.put("customername", "金主爸爸");
        map.put("customertype", "客户类型1");
        // 需求简述
        map.put("demandBrief", "balabala");
        // 合同条款
        map.put("cbmTerms", "巴拉巴拉巴拉");
        //客户要求
        map.put("customerDemand", "这是一个很无理的要求");
        // 财经法务意见
        map.put("financialLegalOpinion", "财经法务表示没意见");
        // 法务意见
        map.put("legalOpinion", "法务表示很无语");
        // 升级处理说明
        map.put("upgradeDealInstruction", "随便升级");
        // 升级处理人（自动显示为特批人）
        map.put("upgradeDealPerson", "李四说我不背锅");
        return map;
    }

    @RequestMapping("/downloadZip")
    public void downloadZip(HttpServletRequest request, HttpServletResponse response) {
        ZipDownLoadUtil.download(request, response);
    }

    @GetMapping("/download")
    public void downloadFiles(HttpServletRequest request, HttpServletResponse response, String[] urls) {

        // 响应头的设置
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");

        // 设置压缩包的名字
        // 解决不同浏览器压缩包名字含有中文时乱码的问题
        String downloadName = System.currentTimeMillis() + ".zip";
        String agent = request.getHeader("USER-AGENT");
        try {
            if (agent.contains("MSIE") || agent.contains("Trident")) {
                downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
            } else {
                downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");

        // 设置压缩流：直接写入response，实现边压缩边下载
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED); // 设置压缩方法
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 循环将文件写入压缩流
        DataOutputStream os = null;
        for (int i = 0; i < urls.length; i++) {
            String fileName = getFileTypeByUrl(urls[i]);
            File file = getFileByUrl(urls[i], fileName);
            try {
                // 添加ZipEntry，并ZipEntry中写入文件流
                // 这里，加上i是防止要下载的文件有重名的导致下载失败
                zipos.putNextEntry(new ZipEntry(fileName));
                os = new DataOutputStream(zipos);
                InputStream is = new FileInputStream(file);
                byte[] b = new byte[100];
                int length = 0;
                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                is.close();
                zipos.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 关闭流
        try {
            os.flush();
            os.close();
            zipos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //url转file
    private File getFileByUrl(String fileUrl, String suffix) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BufferedOutputStream stream = null;
        InputStream inputStream = null;
        File file = null;
        try {
            URL imageUrl = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            file = File.createTempFile("file", suffix);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fileOutputStream);
            stream.write(outStream.toByteArray());
        } catch (Exception e) {
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (stream != null)
                    stream.close();
                outStream.close();
            } catch (Exception e) {
            }
        }
        return file;
    }

    //根据获取文件后缀
    private String getFileTypeByUrl(String url) {

        String suffixes = "3fr|arw|bmp|cr2|crw|dcr|dng|eps|erf|gif|icns|ico|jpeg|jpg|mos|mrw|nef|odd|orf|pdf|pef|png|ppm|ps|psd|raf|raw|svg|svgz|tif|tiff|webp|x3f|xcf|xps|		7z|ace|alz|arc|arj|bz|bz2|cab|cpio|deb|dmg|eml|gz|img|iso|jar|lha|lz|lzma|lzo|rar|rpm|rz|tar|tar.7z|tar.bz|tar.bz2|tar.gz|tar.lzo|tar.xz|tar.Z|tbz|tbz2|tgz|tZ|tzo|xz|z|zip|aac|ac3|aif|aifc|aiff|amr|caf|flac|m4a|m4b|mp3|oga|ogg|sf2|sfark|voc|wav|weba|wma|		3g2|3gp|3gpp|avi|cavs|dv|dvr|flv|gif|m2ts|m4v|mkv|mod|mov|mp4|mpeg|mpg|mts|mxf|ogg|rm|rmvb|swf|ts|vob|webm|wmv|wtv|		abw|djvu|doc|docm|docx|html|lwp|md|odt|pages|pages.zip|pdf|rst|rtf|sdw|tex|txt|wpd|wps|zabw|eps|html|key|key.zip|odp|pdf|pps|ppsx|ppt|pptm|pptx|ps|sda|swf|		csv|html|numbers|numbers.zip|ods|pdf|sdc|xls|xlsm|xlsx|azw|azw3|azw4|cbc|cbr|cbz|chm|docx|epub|fb2|htm|html|htmlz|lit|lrf|mobi|odt|oeb|pdb|pdf|pml|prc|rb|rtf|snb|tcr|txt|txtz|eot|otf|ttf|woff|dwg|dxf|ai|cdr|cgm|emf|eps|pdf|ps|sk|sk1|svg|svgz|vsd|wmf|website";
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");// 正则判断
        Matcher mc = pat.matcher(url);// 条件匹配
        String substring = "";
        while (mc.find()) {
            substring = mc.group();// 截取文件名后缀名
        }
        return substring;
    }

}
