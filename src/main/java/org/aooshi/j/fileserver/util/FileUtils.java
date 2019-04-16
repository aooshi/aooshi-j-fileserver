package org.aooshi.j.fileserver.util;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.util.Properties;

import org.aooshi.j.fileserver.util.ControllerUtils;

public class FileUtils {
    String basePath = "";

    public FileUtils() throws IOException {
        Properties prop = PropertiesLoaderUtils
                .loadAllProperties("application.properties");
        basePath = prop.getProperty("basePath");
        File tempFile = new File(basePath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
    }

    public void Upload(InputStream inputStream, String fileDir, String fileName) throws Exception {
        OutputStream os = null;
        try {
            byte[] bs = new byte[1024];
            int len;
            File tempFile = new File(basePath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            String fileDirPath = basePath + fileDir;
            File file = new File(fileDirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            os = new FileOutputStream(fileDirPath + "/" + fileName);
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } finally {
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void DownLoad(HttpServletRequest request, HttpServletResponse response, String filePath, String downname, Integer download) {
        try {
            File file = new File(basePath + "/" + filePath);
            if (!file.exists()) {
            	ControllerUtils.OutputNotfound(request, response);
                return;
            }
            if(download==1){
                //下载
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downname, "UTF-8"));
            }else if(download==2){
                //打开
                response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(downname, "UTF-8"));
            }
            FileInputStream in = new FileInputStream(basePath + "/" + filePath);
            OutputStream out = response.getOutputStream();
            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception ex) {
        }
    }

    public Boolean Delete(String filePath) {
        File file = new File(basePath + "/" + filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }
}
