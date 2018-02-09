
package com.kuangxf.oauth.server.shiro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class CacheConfiguration {

	private final static Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);

	/*@Bean
	public CacheManager ehCacheManager() {
		EhCacheManagerFactoryBean ehCacheManager = new EhCacheManagerFactoryBean();
		Resource resource = new ClassPathResource("classpath:ehcache/ehcache.xml");
		ehCacheManager.setConfigLocation(resource);
		CacheManager cacheManager = (CacheManager) ehCacheManager.getObject();
		logger.info("------------->初始化CacheManager:{}", cacheManager == null);
		return cacheManager;

	}*/

	@Bean
	public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean) {
		return new EhCacheCacheManager(bean.getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();

		factoryBean.setConfigLocation(new ClassPathResource("ehcache/ehcache.xml"));
		factoryBean.setShared(true);

		return factoryBean;
	}

}
