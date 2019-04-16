package org.aooshi.j.fileserver.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class AppConfiguration {
	
	public static final AppConfiguration singleton = new AppConfiguration();
	
	private String limitMaxRequestSize;
	private String limitMaxFileSize;
	
		
	public String getLimitMaxRequestSize() {
		return limitMaxRequestSize;
	}

	public String getLimitMaxFileSize() {
		return limitMaxFileSize;
	}

	private AppConfiguration()
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
			// fileserver.limit.maxrequestsize =10M
			// fileserver.limit.maxfilesize =50M
			 
			this.limitMaxFileSize  =  props.getProperty("fileserver.limit.maxrequestsize");
			this.limitMaxRequestSize =  props.getProperty("fileserver.limit.maxrequestsize");	        
		 }
	}
}
