package org.aooshi.j.fileserver.util;

import org.aooshi.j.util.StringHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;

public class FileUtils {
    String fileBasePath = "";

    public FileUtils() throws IOException {
//        Properties prop = PropertiesLoaderUtils
//                .loadAllProperties("application.properties");
//        fileBasePath = prop.getProperty("fileBasePath");

        //
        this.fileBasePath = AppConfiguration.singleton.getBasePath();
        if (this.fileBasePath == null || this.fileBasePath == "")
        {
            throw new IOException("no configuration basePath");
        }

        //eq:
        if (this.fileBasePath == "/" || this.fileBasePath == "\\")
        {
            throw new IOException("basePath invalid");
        }

        //first
        char chr = this.fileBasePath.charAt(0);
        if (chr != '/' && chr != '\\')
        {
            throw new IOException("basePath begin character invalid");
        }

        //last
        chr = this.fileBasePath.charAt( this.fileBasePath.length() - 1 );
        if (chr != '/' && chr != '\\')
        {
            throw new IOException("basePath end character invalid");
        }

        //
        this.fileBasePath =  this.fileBasePath + "files/";

        //
        File tempFile = new File(this.fileBasePath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
    }

    public void Upload(InputStream inputStream, String fileDir, String fileName) throws Exception {
        OutputStream os = null;
        try {
            byte[] bs = new byte[4096];
            int len;
//            File tempFile = new File(fileBasePath);
//            if (!tempFile.exists()) {
//                tempFile.mkdirs();
//            }

            String fileDirPath = this.fileBasePath + fileDir;
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
        FileInputStream in = null;
        OutputStream out = null;
        try {

            //mime
            int lastDot = filePath.lastIndexOf('.');
            if (lastDot > 0 && lastDot < filePath.length() - 1)
            {
                String suffix = filePath.substring(lastDot + 1);
                String mime = MimeConfiguration.instance.get(suffix.toLowerCase());
                if (StringHelper.isEmpty(mime) == false) {
                    response.setContentType(mime);
                }
            }
            else
            {
                ControllerUtils.OutputNotfound(request, response);
                response.getOutputStream().print("Not Found");
                return;
            }

            //down
            in = new FileInputStream(this.fileBasePath + "/" + filePath);
            out = response.getOutputStream();

            if (download == 1) {
                //下载
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downname, "UTF-8"));
            } else if (download == 2) {
                //打开
                //response.setCharacterEncoding("UTF-8");
                //response.setContentType("text/plain; charset=utf-8");
                response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(downname, "UTF-8"));
            }

            byte buffer[] = new byte[4096];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {

            ControllerUtils.OutputNotfound(request, response);
            try {
                response.getOutputStream().print("Not Found");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (Exception ex) {
            //
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean Delete(String filePath) {
        File file = new File(this.fileBasePath + "/" + filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }
}
