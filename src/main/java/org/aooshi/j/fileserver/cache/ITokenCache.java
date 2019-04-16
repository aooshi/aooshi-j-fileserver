package org.aooshi.j.fileserver.cache;

import org.aooshi.j.fileserver.entity.TokenInfo;

public interface ITokenCache
{
	void addToken(TokenInfo tokenInfo);
	
	TokenInfo getToken(String token);
	
	boolean removeToken(String token);
}
