package org.aooshi.j.fileserver.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class AppConfiguration {
	
	public static final AppConfiguration singleton = new AppConfiguration();
	
	private String limitMaxRequestSize;
	private String limitMaxFileSize;
	private String basePath;
	private String userHandler;
		
	public String getLimitMaxRequestSize() {
		return limitMaxRequestSize;
	}

	public String getLimitMaxFileSize() {
		return limitMaxFileSize;
	}

	public String getBasePath() {
		return basePath;
	}

	public String getUserHandler() {
		return userHandler;
	}

	private AppConfiguration()
	{	
		 Properties props = null;
		 
		 try {
			props = PropertiesLoaderUtils.loadAllProperties("application.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 if (props != null)
		 {
			// fileserver.limit.maxrequestsize =10M
			// fileserver.limit.maxfilesize =50M
			 
			this.limitMaxFileSize  =  props.getProperty("fileserver.limit.maxfilesize");
			this.limitMaxRequestSize =  props.getProperty("fileserver.limit.maxrequestsize");
			this.basePath = props.getProperty("basePath");
			this.userHandler = props.getProperty("fileserver.user.handler");
		 }
	}
}
