package org.aooshi.j.fileserver.controller;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aooshi.j.fileserver.util.AppConfiguration;
import org.aooshi.j.fileserver.util.AppV;
import org.aooshi.j.util.StringHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@EnableAutoConfiguration
public class Home {

	@GetMapping("/")
	@ResponseBody
	public String index() {

		String n = "File Server";
		String v = AppV.GetApplicationV(n, "");

		return v;
	}

	@GetMapping("/fsatoken")
	@ResponseBody
	public String FsaTokenAuth(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(name = "token") String token) {
		
		response.setHeader("Content-Type", "application/x-javascript;");
		
		if (StringHelper.isEmpty(token))
		{
			response.setStatus(400);
			return "/* token empty */";
		}

		if (token.matches("^[a-zA-Z0-9]{1,50}$") == false)
		{
			response.setStatus(400);
			return "/* token error */";
		}

		Cookie cookie = org.springframework.web.util.WebUtils.getCookie(request, "fsatoken");
		if (cookie == null || token.equals(cookie.getValue()) == false)
		{
			cookie = new Cookie("fsatoken", token);
			cookie.setPath("/");			
			//
			response.addCookie(cookie);
		}
						
		return "/* ok */";
		
		//String cv = "fsatoken="+ token;
		//return "if (document.cookie.indexOf('"+cv+"') == -1) document.cookie = \""+ cv +"\";";
	}
}
