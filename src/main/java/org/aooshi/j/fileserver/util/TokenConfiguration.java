package org.aooshi.j.fileserver.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class TokenConfiguration {
	
	public static final TokenConfiguration singleton = new TokenConfiguration();
	
	private String handler;

	//unit seconds
	private int expire;


	public String getHandler() {
		return this.handler;
	}

	public int getExpire() {
		return this.expire;
	}
	
	private TokenConfiguration()
	{	
		 Properties props = null;
		 
		 try {
			props = PropertiesLoaderUtils
			            .loadAllProperties("application.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 if (props != null)
		 {
			this.handler  =  props.getProperty("fileserver.token.handler");
			this.expire =  Integer.parseInt(props.getProperty("fileserver.token.expire"));	        
		 }
	}
}
