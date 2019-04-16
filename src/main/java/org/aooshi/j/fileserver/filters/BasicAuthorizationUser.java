package org.aooshi.j.fileserver.filters;

public class BasicAuthorizationUser
{
	private int uid;
	private String pwd;
	private long expire;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
	}	
}
