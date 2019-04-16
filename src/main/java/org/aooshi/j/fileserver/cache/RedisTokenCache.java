package org.aooshi.j.fileserver.cache;

import org.aooshi.j.fileserver.entity.TokenInfo;
import org.aooshi.j.fileserver.util.JedisPoolUtil;
import org.aooshi.j.util.DateHelper;
import org.aooshi.j.util.StringHelper;

import redis.clients.jedis.Jedis;

public class RedisTokenCache implements ITokenCache {
		
	@Override
	public void addToken(TokenInfo tokenInfo) {
		String json = tokenInfo.toJson();
		String token = tokenInfo.getToken();
		int ttl = tokenInfo.getTtl();
		Jedis jedis = JedisPoolUtil.getJedis();
		try
		{
			jedis.setex(token, ttl, json);
		}
		finally
		{
			JedisPoolUtil.release(jedis);
		}
	}

	@Override
	public TokenInfo getToken(String token) {
		Jedis jedis = JedisPoolUtil.getJedis();
		//
		TokenInfo tokenInfo = null;
		//Long now = System.currentTimeMillis();
		Long now = DateHelper.getTimestamp();
		try
		{
			String json =jedis.get(token);
			if (StringHelper.isEmpty(json))
			{
			}
			else
			{
				tokenInfo = TokenInfo.formJson(json);
				if (tokenInfo != null && now > tokenInfo.getExpires())
				{
					jedis.del(token);
					tokenInfo = null;
				}
			}
		}
		finally
		{
			JedisPoolUtil.release(jedis);
		}
		return tokenInfo;
	}

	@Override
	public boolean removeToken(String token) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long deleted = 0L;
		try
		{
			deleted = jedis.del(token);
		}
		finally
		{
			JedisPoolUtil.release(jedis);
		}
		
		return deleted > 0;
	}

}
