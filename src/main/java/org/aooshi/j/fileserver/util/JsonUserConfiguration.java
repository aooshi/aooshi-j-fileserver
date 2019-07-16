package org.aooshi.j.fileserver.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.aooshi.j.fileserver.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonUserConfiguration {

	public static final JsonUserConfiguration singleton = new JsonUserConfiguration();

	private JSONObject jsonObject = null;
	private String configFile = "";
	private Map<Integer, User> userMap = Collections.synchronizedMap(new HashMap<Integer,User>());

	private JsonUserConfiguration()
	{
		this.configFile = AppConfiguration.singleton.getBasePath() + "users.json";

		this.load();
	}

	public void load()
	{
		File file = new File( this.configFile );
		if (file.exists()) {

			String jsonString = this.readFileToString(file.getAbsolutePath());
			if (jsonString != null && jsonString != "")
			{
				JSONArray jsonArray = JSONArray.fromObject(jsonString);
				List<User> list = JSONArray.toList(jsonArray,new User(),new JsonConfig());
				//
				Map<Integer, User> userMap = Collections.synchronizedMap(new HashMap<Integer,User>());
				for (User u : list)
				{
					userMap.put(u.getUid(), u);
				}

				//
				this.userMap = userMap;
			}
		}
	}

	/**
	 * @param uid
	 * @return
	 */
	public User getUser(int uid)
	{
		User user = this.userMap.get(uid);
		return user;
	}

	private String readFileToString(String fileName) {
		String encoding = "UTF-8";
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			in.read(filecontent);
			return new String(filecontent, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
