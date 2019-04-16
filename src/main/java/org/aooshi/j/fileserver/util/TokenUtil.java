package org.aooshi.j.fileserver.util;

import java.util.UUID;

import org.aooshi.j.fileserver.cache.ITokenCache;
import org.aooshi.j.fileserver.cache.LocalTokenCache;
import org.aooshi.j.fileserver.cache.RedisTokenCache;
import org.aooshi.j.fileserver.entity.TokenInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.aooshi.j.util.DateHelper;


public class TokenUtil {
	
	private static ITokenCache tokenCache = loadTokenCache();
	public static ITokenCache getTokenCache()
	{
		return TokenUtil.tokenCache;
	}
	private static ITokenCache loadTokenCache()
	{
		ITokenCache tc = null;
		if ("redis".equalsIgnoreCase(TokenConfiguration.singleton.getHandler()))
		{
			tc = new RedisTokenCache();
		}
		else
		{
			tc = new LocalTokenCache();
		}
		return tc;
	}

	public static TokenInfo newAccessToken(String[] paths, String fsatoken, int ttl)
	{
		String token = "a" + TokenUtil.createToken();
		ITokenCache tokenCache = getTokenCache();
		if (ttl < 1)
			ttl = TokenConfiguration.singleton.getExpire();
		long expires = DateHelper.getTimestamp() + ttl;
						
		TokenInfo accessToken = TokenInfo.getAccessToken(token);
		accessToken.setExpires(expires);
		accessToken.setTtl(ttl);
		accessToken.setObjects(paths);
		accessToken.setFsatoken(fsatoken == null ? "" : fsatoken);
		tokenCache.addToken(accessToken);
		return accessToken;
	}
	
	public static TokenInfo newUploadToken(String[] buckets)
	{
		String token = "u" + TokenUtil.createToken();
		ITokenCache tokenCache = getTokenCache();
		int ttl = TokenConfiguration.singleton.getExpire();
		long expires = DateHelper.getTimestamp() + ttl;
		
		TokenInfo uploadToken = TokenInfo.getUploadToken(token);
		uploadToken.setExpires(expires);
		uploadToken.setTtl(ttl);
		uploadToken.setObjects(buckets);
		tokenCache.addToken(uploadToken);
		return uploadToken;
	}
	
	public static boolean removeToken(String token)
	{
		ITokenCache tokenCache = getTokenCache();
		return tokenCache.removeToken(token);
	}
	
	public static TokenInfo getTokenInfo(String token)
	{
		ITokenCache tokenCache = getTokenCache();
		TokenInfo tokenInfo = tokenCache.getToken(token);
		return tokenInfo;
	}
			
	private static String createToken()
	{
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		
		Long time =  System.currentTimeMillis();
		String timeString = time.toString();
		
		String timemd5 = DigestUtils.md5Hex(timeString);
		
		String token = uuid + timemd5.substring(0, 7);
		
		//length : 39
		return token;
	}	
	
}
