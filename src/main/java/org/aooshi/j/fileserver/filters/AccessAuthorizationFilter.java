package org.aooshi.j.fileserver.filters;

import org.aooshi.j.fileserver.entity.TokenInfo;
import org.aooshi.j.fileserver.util.PublicBucketConfiguration;
import org.aooshi.j.fileserver.util.TTLConfiguration;
import org.aooshi.j.fileserver.util.TokenUtil;
import org.aooshi.j.util.StringHelper;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

public class AccessAuthorizationFilter implements Filter {
	
	@Override
	public void destroy() {
		
	}
 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		//
		String token = req.getParameter("token");
		String path = req.getRequestURI();
		String[] paths = path.split("/", 4);
		//
		boolean checkSuccess = true;
		checkSuccess = checkSuccess && paths.length > 3;
		if (StringHelper.isEmpty(token) == false)
			checkSuccess = checkSuccess && StringHelper.isLetterOrDigit(token) == true;
		checkSuccess = checkSuccess && this.checkToken(req,resp,token,paths) == true;
		
		if (checkSuccess == true)
		{
			// /a/bucket/path1/path2/filename
			request.setAttribute("RequestURI", path);
			request.getRequestDispatcher("/fileaccess").forward(request,response);

			//chain.doFilter(request, response);
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

	private boolean checkToken(HttpServletRequest request,HttpServletResponse response,String token, String[] paths) throws ServletException, IOException {

		/*

		/a/bucket/path1/path2/filename
		String a = paths[1];

		*/

		String bucket = paths[2];

		Boolean checkResult = false;
		if (PublicBucketConfiguration.instance.contains(bucket) == true)
		{
			String bucketTTL = TTLConfiguration.instance.get(bucket);
			if (bucketTTL != null && bucketTTL != "")
			{
				response.setHeader("Cache-Control", "max-age=" + bucketTTL);
			}
			return true;
		}
		else
		{
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
		}

		if (StringHelper.isEmpty(token))
		{
			return false;
		}

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
		}

		return checkResult;
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
		//
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

	}
}
