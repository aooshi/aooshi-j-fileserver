package org.aooshi.j.fileserver.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class AppConfiguration {

	public static final AppConfiguration singleton = new AppConfiguration();

	private final Logger logger = LogManager.getLogger(AppConfiguration.class);

	private String limitMaxRequestSize;
	private String limitMaxFileSize;
	private String basePath;
	private String userHandler;
	private String tokenHandler;
	private int tokenExpire;

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

	public String getTokenHandler() {
		return tokenHandler;
	}

	public int getTokenExpire() {
		return tokenExpire;
	}

	private AppConfiguration() {
		Properties props = null;

		try {
			props = PropertiesLoaderUtils.loadAllProperties("application.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (props != null) {
			// fileserver.limit.maxrequestsize =10M
			// fileserver.limit.maxfilesize =50M

			this.limitMaxFileSize = props.getProperty("fileserver.limit.maxfilesize");
			this.limitMaxRequestSize = props.getProperty("fileserver.limit.maxrequestsize");
			this.basePath = props.getProperty("basePath");
			this.userHandler = props.getProperty("fileserver.user.handler");

			//
			this.tokenHandler = props.getProperty("fileserver.token.handler");
			this.tokenExpire = Integer.parseInt(props.getProperty("fileserver.token.expire"));
			// =86400

			//
			this.logger.info("fileserver.limit.maxfilesize: " + this.limitMaxFileSize);
			this.logger.info("fileserver.limit.maxrequestsize: " + this.limitMaxRequestSize);
			this.logger.info("fileserver.limit.basePath: " + this.basePath);
			this.logger.info("fileserver.limit.fileserver.user.handler: " + this.userHandler);
			this.logger.info("fileserver.token.handler: " + this.tokenHandler);
			this.logger.info("fileserver.token.expire: " + this.tokenExpire);
		}
	}
}
