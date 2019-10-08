package org.aooshi.j.fileserver.util;

import org.springframework.boot.system.ApplicationHome;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AppV {

	/**
	 * get application flag
	 *
	 * @return
	 */
	public static String GetApplicationTime() {
		String lastModifiedString = "";
		try {
			ApplicationHome h = new ApplicationHome(AppV.class);
			long lastModified = h.getSource().lastModified();

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(lastModified);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			lastModifiedString = formatter.format(cal.getTime());

		} catch (Exception e) {
			lastModifiedString = "";
		}

		return lastModifiedString;
	}

	/**
	 * get application v
	 *
	 * @param name
	 * @param line
	 * @return
	 */
	public static String GetApplicationV(String name, String line) {
		String applicationTime = GetApplicationTime();
		StringBuilder builder = new StringBuilder();
		//
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		builder.append("G M T: " + formatter.format(now));
		builder.append("\r\n");
		//
		formatter.setTimeZone(TimeZone.getDefault());
		builder.append("LOCAL: " + formatter.format(now));
		builder.append("\r\n");
		//
		if (line != null && "".equals(line) != true) {
			builder.append(line);
		}
		//
		builder.append("\r\n");
		builder.append(name);
		builder.append(" / ");
		builder.append(applicationTime);
		builder.append("\r\n");
		//
		String result = builder.toString();
		return result;
	}

}
