package org.aooshi.j.fileserver.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerUtils {


	public static void OutputForbidden(HttpServletRequest request,HttpServletResponse response)
	{
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("text/plain; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}
	public static void OutputNotfound(HttpServletRequest request,HttpServletResponse response)
	{
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("text/plain; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
}
