package org.aooshi.j.fileserver.cache;

import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.aooshi.j.fileserver.entity.TokenInfo;
import org.aooshi.j.fileserver.util.TokenConfiguration;
import org.aooshi.j.fileserver.util.TokenUtil;
import org.aooshi.j.util.DateHelper;

public class LocalTokenCache implements ITokenCache
{	
	static Cache<String, TokenInfo> cacheContainer = getCacheContainer();
	
	private static Cache<String, TokenInfo> getCacheContainer()
	{
		CachingProvider cachingProvider = Caching.getCachingProvider();
	    CacheManager cacheManager = cachingProvider.getCacheManager();
	    MutableConfiguration<String, TokenInfo> tokenConfig = new MutableConfiguration<String, TokenInfo>();	    
	    //tokenConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
	    Duration duration = new Duration(TimeUnit.SECONDS,TokenConfiguration.singleton.getExpire());
	    //Duration duration = new Duration(TimeUnit.SECONDS,10);
	    tokenConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
	    Cache<String, TokenInfo> cache = cacheManager.createCache("jfileserver.token",tokenConfig);
	    return cache;
	}
	
	@Override
	public void addToken(TokenInfo tokenInfo) {
		
		String token = tokenInfo.getToken();
		synchronized (cacheContainer) 
		{
			cacheContainer.put(token, tokenInfo);
		}
	}

	@Override
	public TokenInfo getToken(String token) {

		TokenInfo tokenInfo = null;
		Long now = DateHelper.getTimestamp();
		synchronized (cacheContainer) 
		{
			tokenInfo = cacheContainer.get(token);
			if (tokenInfo != null && now > tokenInfo.getExpires())
			{
				cacheContainer.remove(token);
				tokenInfo = null;
			}
		}
		return tokenInfo;
	}

	@Override
	public boolean removeToken(String token) {
		boolean removed = false;
		synchronized (cacheContainer) 
		{
			removed = cacheContainer.remove(token);
		}
		return removed;
	}
		

	
	public void printKey()
	{
		synchronized (cacheContainer) {

			cacheContainer.forEach(e -> {
				if (e == null)
					System.out.println("(null)");
				else
					System.out.println("(KEY:" + e.getKey() + ")=>[VAL:"+ e.getValue()+"]");
			});			
		}
	}
	
	
	public static void Test()
	{
		LocalTokenCache fsa = new LocalTokenCache();		
		TestThread thread = fsa.new TestThread();
	    thread.start();
	}
	
	 class TestThread extends Thread
	 {
        @SuppressWarnings("static-access")
		@Override
        public void run() {
            int i = 0;
            

       	String[] arr = new String[]{"a"};
       	
       	//TokenInfo newAccessToken = TokenUtil.newAccessToken(arr);
       	TokenUtil.newAccessToken(arr,"",0);
       	TokenUtil.newAccessToken(arr,"",0);
       	TokenUtil.newAccessToken(arr,"",0);
       	TokenUtil.newAccessToken(arr,"",0);
       	TokenUtil.newAccessToken(arr,"",0);
       	TokenUtil.newAccessToken(arr,"",0);
            
            while(i<Integer.MAX_VALUE){
            	
            	
            	LocalTokenCache tokenCache = (LocalTokenCache)TokenUtil.getTokenCache();

            	   try {
            		   
            		   TokenUtil.newAccessToken(arr,"",0);
            		   tokenCache.printKey();
            		   
                       Thread.currentThread().sleep(1000);
                   } catch (InterruptedException e) {
                        
                   }
            	
                System.out.println(i+" while循环");
                i++;
            }
        }
    }
	
}
