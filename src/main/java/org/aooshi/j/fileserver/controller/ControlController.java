package org.aooshi.j.fileserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aooshi.j.fileserver.entity.TokenInfo;
import org.aooshi.j.fileserver.util.FileUtils;
import org.aooshi.j.fileserver.util.TokenUtil;
import org.aooshi.j.util.PathHelper;
import org.aooshi.j.util.StringHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@EnableAutoConfiguration
public class ControlController {

    @ResponseBody
    @PostMapping("/control/delete")
    public Boolean Delete(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String path = request.getParameter("path");
        try {
            FileUtils fu = new FileUtils();
            return fu.Delete(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/control/upload")
    public String Upload(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String bucket = request.getParameter("bucket");
        String path = request.getParameter("path");

        if (file == null)
        {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "No file";
        }


        if (file == null)
        {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "No file";
        }

        //原始文件扩展名
        String extension = PathHelper.getExtension(file.getOriginalFilename());
        //路径+随机名.扩展名
        String filePath = "";
        //
        if (StringHelper.isEmpty(path) == false) {
            String regEx = "^\\/(\\w+\\/?)+\\/$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(path);
            boolean rs = matcher.matches();
            if (rs == false) {
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "Path invalid";
            }
            filePath = path + extension;
        } else {
            filePath = PathHelper.getSecondPath(extension);
        }

        //随机名.扩展名
        String fileName = PathHelper.getFileNameByPath(filePath);
        //路径
        //String fileDir=filePath.replace(fileName, "");
        String fileDir= PathHelper.getDirectoryByPath(filePath);
        //
        String result = "/" + bucket + "/" + filePath;

        FileUtils fu = new FileUtils();
        fu.Upload(file.getInputStream(), bucket + "/" + fileDir, fileName);

        return result;
    }


    @ResponseBody
    @GetMapping("/control/getaccesstoken")
    public String GetAccessToken(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(name = "path") String path
            , @RequestParam(name = "fsatoken", required=false) String fsatoken
            , @RequestParam(name = "ttl", required=false) Integer ttl) {
    	
    	//fsatoken
    	if (fsatoken == null)
    		fsatoken = "";
    	else if (fsatoken.length() > 255)
    		fsatoken = "";
    	
    	if (ttl == null)
    		ttl = 0;
    	
    	//
        String[] paths = path.split(",");
        TokenInfo accessToken = TokenUtil.newAccessToken(paths,fsatoken,ttl);
        String token = accessToken.getToken();
        return token;
    }

    @ResponseBody
    @GetMapping("/control/getuploadtoken")
    public String GetUploadToken(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(name = "bucket") String bucket) {
        String[] buckets = bucket.split(",");
        TokenInfo uploadToken = TokenUtil.newUploadToken(buckets);
        String token = uploadToken.getToken();
        return token;
    }
}
