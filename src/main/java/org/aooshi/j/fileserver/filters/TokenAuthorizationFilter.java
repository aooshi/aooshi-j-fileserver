package org.aooshi.j.fileserver.filters;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aooshi.j.fileserver.entity.TokenInfo;
import org.aooshi.j.fileserver.util.TokenUtil;
import org.aooshi.j.util.StringHelper;

public class TokenAuthorizationFilter implements Filter {
	
	@Override
	public void destroy() {
		
	}
 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {


		HttpServletRequest req = (HttpServletRequest)request;

		if("OPTIONS".equalsIgnoreCase(req.getMethod())){
			chain.doFilter(request, response);
			//skip check
			return;
		}

		String token = req.getParameter("token");
		boolean checkSuccess = StringHelper.isEmpty(token) == false &&
				StringHelper.isLetterOrDigit(token) == true &&
				this.checkToken(req, token) == true;

		if (checkSuccess == true)
		{
			chain.doFilter(request, response);
		}
		else
		{
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setCharacterEncoding("UTF-8");
			//httpResponse.setContentType("application/json; charset=utf-8");
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				//
			httpResponse.getWriter().write("Unauthorized");
		}
	}

	private boolean checkToken(HttpServletRequest request,String token) {

		Boolean checkResult = false;
		TokenInfo tokenInfo = TokenUtil.getTokenInfo(token);
		
		if (tokenInfo != null)
		{
			if (tokenInfo.getTokenType() == TokenInfo.TYPE_ACCESS)
			{
				Cookie fsatokenCookie = org.springframework.web.util.WebUtils.getCookie(request, "fsatoken");
				String fsatoken = "";
				if (fsatokenCookie != null)
				{
					fsatoken = fsatokenCookie.getValue();
				}
				
				//
				checkResult = this.checkAccessToken(request, tokenInfo, fsatoken);
				if (checkResult == true)
				{
					if (StringHelper.isEmpty(tokenInfo.getFsatoken()))
					{
						TokenUtil.removeToken(token);
					}
				}
			}
			else if (tokenInfo.getTokenType() == TokenInfo.TYPE_UPLOAD)
			{
				checkResult = this.checkUploadToken(request, tokenInfo);
				if (checkResult == true)
				{
					TokenUtil.removeToken(token);
				}
			}
		}

		return checkResult;
	}

	private Boolean checkUploadToken(HttpServletRequest request, TokenInfo tokenInfo) {
		//check bucket
		String bucket = request.getParameter("bucket");
		if (StringHelper.isEmpty(bucket))
		{
			return false;
		}
		String[] bucketArray = bucket.split(",");
		//
		HashSet<String> hashSet = new HashSet<String>();
		for(int i=0,l = bucketArray.length ; i<l;i++)
		{
			hashSet.add(bucketArray[i].toLowerCase().trim());	
		}
		//
		String[] paths = tokenInfo.getObjects();
		if (paths != null)
		{
			for(int i=0,l=paths.length;i<l;i++)
			{
				String item = paths[i].toLowerCase().trim();
				if (hashSet.contains(item))
				{
					return true;
				}
			}
		}
		//
		return false;
	}

	private Boolean checkAccessToken(HttpServletRequest request,TokenInfo tokenInfo, String fsatoken) {
		//check path
		String path = request.getParameter("path");
		if (StringHelper.isEmpty(path))
		{
			return false;
		}
		//check fas token
		String fsatoken2 = tokenInfo.getFsatoken();
		if (StringHelper.isEmpty(fsatoken2) == false)
		{
			if (StringHelper.isEmpty(fsatoken) == false && fsatoken.equals(fsatoken2))
			{
			}
			else
			{
				return false;
			}
		}
		
		String[] pathArray = path.split(",");
		//
		HashSet<String> hashSet = new HashSet<String>();
		for(int i=0,l = pathArray.length ; i<l;i++)
		{
			hashSet.add(pathArray[i].toLowerCase().trim());	
		}
		//
		String[] paths = tokenInfo.getObjects();
		if (paths != null)
		{
			for(int i=0,l=paths.length;i<l;i++)
			{
				String item = paths[i].toLowerCase().trim();
				if (hashSet.contains(item))
				{
					return true;
				}
			}
		}
		//
		return false;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		//System.out.println("TokenAuthorizationFilter init");
	}
}
