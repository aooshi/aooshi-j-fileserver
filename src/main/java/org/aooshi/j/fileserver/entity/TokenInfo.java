package org.aooshi.j.fileserver.entity;

import net.sf.json.JSONObject;

public class TokenInfo implements java.io.Serializable {
	
	private static final long serialVersionUID = 3723302549589610L;
	
	public final static int TYPE_UPLOAD = 1;
	public final static int TYPE_ACCESS = 2;

	private int tokenType = 0;
	private long expires = 0;
	private int ttl = 0;

	private String token = "";
	private String fsatoken = "";
	private int limit = 0;
	private String[] objects = new String[0];
	
	public TokenInfo()
	{
	}
	
	public static TokenInfo getUploadToken(String token)
	{		
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.tokenType = TYPE_UPLOAD;
		tokenInfo.limit = 1;
		tokenInfo.expires = 86400; //1 day
		tokenInfo.token = token;
		return tokenInfo;
	}
	public static TokenInfo getAccessToken(String token)
	{
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.tokenType = TYPE_ACCESS;
		tokenInfo.limit = 1;
		tokenInfo.expires = 86400; //1 day
		tokenInfo.token = token;
		return tokenInfo;
	}	
	/*public static TokenInfo getAccessToken(String token,String uid,String sid)
	{
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.tokenType = TYPE_ACCESS;
		tokenInfo.limit = 1;
		tokenInfo.expires = 86400; //1 day
		tokenInfo.token = token;
		tokenInfo.sid = sid;
		tokenInfo.uid = uid;
		return tokenInfo;
	}*/
		
	public int getTokenType() {
		return tokenType;
	}
	public void setTokenType(int tokenType) {
		this.tokenType = tokenType;
	}
	
	public long getExpires() {
		return expires;
	}
	public void setExpires(long expires) {
		this.expires = expires;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	 

	public String[] getObjects() {
		return objects;
	}

	public void setObjects(String[] objects) {
		this.objects = objects;
	}
	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public String getFsatoken() {
		return fsatoken;
	}

	public void setFsatoken(String fsatoken) {
		this.fsatoken = fsatoken;
	}

	public String toJson()
	{
		JSONObject json = JSONObject.fromObject(this);
		return json.toString();
	}
	
	public static TokenInfo formJson(String json)
	{
		JSONObject jsonObject = JSONObject.fromObject(json);
		TokenInfo tokenInfo = (TokenInfo)JSONObject.toBean(jsonObject,TokenInfo.class);
		return tokenInfo;
	}
	
}
