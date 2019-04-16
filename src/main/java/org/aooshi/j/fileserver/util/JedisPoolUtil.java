package org.aooshi.j.fileserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
	private static JedisPool pool = null;
	static {
		//load config
		InputStream in = JedisPoolUtil.class.getClassLoader()
				.getResourceAsStream("redis.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// 最大连接数
		poolConfig.setMaxTotal(Integer.parseInt(pro.get("redis.maxTotal")
				.toString()));
		// 最大空闲连接数
		poolConfig.setMaxIdle(Integer.parseInt(pro.get("redis.maxIdle")
				.toString()));
		// 最小空闲连接数
		poolConfig.setMinIdle(Integer.parseInt(pro.get("redis.minIdle")
				.toString()));
		
		pool = new JedisPool(poolConfig, pro.get("redis.url").toString(),
				Integer.parseInt(pro.get("redis.port").toString()));
	}

	public static Jedis getJedis() {
		Jedis resource = pool.getResource();
		return resource;
	}
	
	public static void release(Jedis jedis) {
		if (jedis != null)
		{
			//pool.returnResource(redis);
			jedis.close();
		}
	}
}
