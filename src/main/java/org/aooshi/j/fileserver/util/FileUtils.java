package org.aooshi.j.fileserver.util;

import org.aooshi.j.util.StringHelper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        RandomAccessFile in = null;
        OutputStream out = null;
        try {

            //mime
            int lastDot = filePath.lastIndexOf('.');
            if (lastDot > 0 && lastDot < filePath.length() - 1) {
                String suffix = filePath.substring(lastDot + 1);
                String mime = MimeConfiguration.instance.get(suffix.toLowerCase());
                if (StringHelper.isEmpty(mime) == false) {
                    response.setContentType(mime);
                }
            } else {
                ControllerUtils.OutputNotfound(request, response);
                response.getOutputStream().print("Not Found");
                return;
            }

            if (download == 1) {
                //下载
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downname, "UTF-8"));
            } else if (download == 2) {
                //打开
                response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(downname, "UTF-8"));
            }

            //
//            String range = request.getHeader("Range");
//            int start = 0, end = 0;
//            if(range != null && range.startsWith("bytes=")){
//                String[] values = range.split("=")[1].split("-");
//                start = Integer.parseInt(values[0]);
//                if(values.length > 1){
//                    end = Integer.parseInt(values[1]);
//                }
//            } else {
//                range = null;
//            }
//
//            //file
//            File file = new File(this.fileBasePath + "/" + filePath);
//            long fileLastModified = file.lastModified();
//
            //down
            in = new RandomAccessFile(this.fileBasePath + "/" + filePath, "r");//只读模式
            out = response.getOutputStream();
//
//            if (range != null) {
//                this.DoDownLoadRange(request, response, in, out, start, end, fileLastModified);
//            } else {
//                this.DoDownLoad(request, response, in, out);
//            }

            this.DoDownLoad(request, response, in, out);

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

    private void DoDownLoadRange(HttpServletRequest request, HttpServletResponse response, RandomAccessFile in, OutputStream out,int start,int end, long fileLastModified) throws IOException {
        //in.seek(0);
        long contentLength = in.length();

        //
        int requestSize = 0;
        if (end != 0 && end > start) {
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }

        //
        Long time = contentLength + fileLastModified;
        Date lastModified = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        String lastModifiedString = sdf.format(lastModified);

        //
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", time.hashCode() + "");
        response.setHeader("Last-Modified", lastModifiedString);

        //断点续传的方式来返回
        //206
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        //
//        long requestStart = 0, requestEnd = 0;
//        String[] ranges = range.split("=");
//        if(ranges.length > 1){
//            String[] rangeDatas = ranges[1].split("-");
//            requestStart = Integer.parseInt(rangeDatas[0]);
//            if(rangeDatas.length > 1){
//                requestEnd = Integer.parseInt(rangeDatas[1]);
//            }
//        }
        long length = 0;
        if (start > 0) {
            length = end - start + 1;
            response.setContentLengthLong(length);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + contentLength);
        } else {
            length = end - start;
            response.setContentLengthLong(length);
            response.setHeader("Content-Range", "bytes " + start + "-" + (contentLength - 1) + "/" + contentLength);
        }

        //
        int needSize = requestSize;
        in.seek(start);
        while (needSize > 0) {
            byte[] buffer = new byte[4096];
            int len = in.read(buffer);
            if (needSize < buffer.length) {
                //out.write(buffer, 0, needSize);
                out.write(buffer, 0, len);
            } else {
                out.write(buffer, 0, len);
                if (len < buffer.length) {
                    break;
                }
            }
            needSize -= buffer.length;
        }
    }

    private void DoDownLoad(HttpServletRequest request, HttpServletResponse response, RandomAccessFile in, OutputStream out) throws IOException {
        in.seek(0);
        long contentLength = in.length();

        //
        response.setContentLengthLong(contentLength);

        //
        byte buffer[] = new byte[4096];
        int len = 0;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
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
