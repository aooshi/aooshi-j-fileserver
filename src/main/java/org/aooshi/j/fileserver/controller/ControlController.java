package org.aooshi.j.fileserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aooshi.j.fileserver.entity.TokenInfo;
import org.aooshi.j.fileserver.util.FileUtils;
import org.aooshi.j.fileserver.util.TokenUtil;
import org.aooshi.j.util.PathHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        String suffix = "";

        //
        int lastIndex = file.getOriginalFilename().lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < file.getOriginalFilename().length())
        {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "BAD_REQUEST Path suffix";
        }
        suffix = file.getOriginalFilename().substring(lastIndex);
        if (suffix == "" || suffix == ".")
        {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "BAD_REQUEST Path suffix";
        }

        //
        String filedir = "";
        String filename = "";

        //
        if (null != path && path.equals("") != false) {

            char chr = path.charAt(0);
            if (chr == '/' || chr == '\\' )
            {
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "BAD_REQUEST Path";
            }

            String regEx = "^\\/(\\w+\\/?)+\\/$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(path);
            boolean rs = matcher.matches();
            if (rs == false) {
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "BAD_REQUEST Path";
            }

            //
            String[] paths = path.split("/");
            filename = paths[paths.length - 1];
            filedir = "";
            for(int i=0,l = paths.length - 1; i<l; i++)
            {
                filedir += paths[i];
                if ((i == l) == false) {
                    filedir += "/";
                }
            }
            //suffix = suffix;
        } else {
            String[] paths = PathHelper.createSecondPath(suffix);
            filedir = paths[0];
            filename = paths[1];
            suffix = paths[2];
        }

        FileUtils fu = new FileUtils();
        fu.Upload(file.getInputStream(), filedir, filename + suffix);

        String result = bucket + "/" + filedir + "/" + filename + suffix;
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
