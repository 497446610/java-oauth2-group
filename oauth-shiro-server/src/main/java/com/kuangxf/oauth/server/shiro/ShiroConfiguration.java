/*
 * @(#)ShiroConfiguration.java        1.0 2017年9月28日
 *
 */

package com.kuangxf.oauth.server.shiro;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kuangxf.oauth.server.shiro.credentials.RetryLimitHashedCredentialsMatcher;

/**
 * 集成shior.
 *
 * @version 1.0 2017年9月28日
 * @author kuangxf
 * @history
 * 
 */
@Configuration
public class ShiroConfiguration {

	private final static Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager,
			@Qualifier("oauth2AuthenticationFilter") FormAuthenticationFilter formAuthenticationFilter) {
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		bean.setSecurityManager(manager);
		// 配置登录的url和登录成功的url
		bean.setLoginUrl("/login");
		bean.setSuccessUrl("/");
		bean.setFilterChainDefinitions(" / = anon\r\n"//
				+ "                /login = authc\r\n"//
				+ "                /logout = logout\r\n" //
				+ "\r\n" //
				+ "                /authorize=anon\r\n"//
				+ "                /accessToken=anon\r\n"//
				+ "                /userInfo=anon\r\n" //
				+ "\r\n"//
				+ "                /** = user");

		bean.getFilters().put("authc", formAuthenticationFilter);

		return bean;
	}

	// 配置核心安全事务管理器
	@Bean(name = "securityManager")
	public SecurityManager securityManager(@Qualifier("userRealm") UserRealm userRealm,
			@Qualifier("defaultWebSessionManager") DefaultWebSessionManager defaultWebSessionManager,
			@Qualifier("cacheManager") CacheManager cacheManager) {
		logger.info("--------------shiro已经加载----------------");
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(userRealm);
		manager.setSessionManager(defaultWebSessionManager);
		manager.setCacheManager(cacheManager);
		return manager;
	}

	// oauth过滤器
	@Bean(name = "oauth2AuthenticationFilter")
	public FormAuthenticationFilter oauth2AuthenticationFilter(@Qualifier("userRealm") UserRealm userRealm) {
		FormAuthenticationFilter filter = new FormAuthenticationFilter();
		filter.setUsernameParam("username");
		filter.setPasswordParam("password");
		filter.setRememberMeParam("rememberMe");
		filter.setLoginUrl("/login");
		return filter;
	}

	@Bean(name = "defaultWebSessionManager")
	public DefaultWebSessionManager defaultWebSessionManager() {
		DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
		defaultWebSessionManager.setGlobalSessionTimeout(1800000);
		defaultWebSessionManager.setDeleteInvalidSessions(true);
		defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
		defaultWebSessionManager.setSessionIdCookieEnabled(true);

		SimpleCookie sessionIdCookie = new SimpleCookie("sid");
		sessionIdCookie.setHttpOnly(true);
		sessionIdCookie.setMaxAge(-1);

		defaultWebSessionManager.setSessionIdCookie(sessionIdCookie);

		EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
		sessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
		sessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");

		defaultWebSessionManager.setSessionDAO(sessionDAO);

		return defaultWebSessionManager;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		creator.setProxyTargetClass(true);
		return creator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			@Qualifier("securityManager") SecurityManager manager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(manager);
		return advisor;
	}

	@Bean("userRealm")
	public UserRealm userRealm(
			@Qualifier("retryLimitHashedCredentialsMatcher") RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher) {
		UserRealm authRealm = new UserRealm();
		authRealm.setCredentialsMatcher(retryLimitHashedCredentialsMatcher);
		authRealm.setCachingEnabled(true);
		return authRealm;

	}

	@Bean("retryLimitHashedCredentialsMatcher")
	public RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher(
			@Qualifier("cacheManager") CacheManager cacheManager) {
		RetryLimitHashedCredentialsMatcher credentialsMatcher = new RetryLimitHashedCredentialsMatcher(cacheManager);
		credentialsMatcher.setHashAlgorithmName("md5");
		credentialsMatcher.setHashIterations(2);
		credentialsMatcher.setStoredCredentialsHexEncoded(true);
		return credentialsMatcher;
	}
}
