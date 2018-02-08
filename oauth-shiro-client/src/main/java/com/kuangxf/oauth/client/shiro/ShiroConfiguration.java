/*
 * @(#)ShiroConfiguration.java        1.0 2017年9月28日
 *
 */

package com.kuangxf.oauth.client.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
			@Qualifier("oauth2AuthenticationFilter") OAuth2AuthenticationFilter oauth2AuthenticationFilter) {

		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

		bean.setSecurityManager(manager);

		// 配置登录的url和登录成功的url
		bean.setLoginUrl(
				"http://localhost:8080/chapter17-server/authorize?client_id=c1ebe466-1cdc-4bd3-ab69-77c3561b9dee&response_type=code&redirect_uri=http://localhost:9001/client/oauth2-login");
		bean.setSuccessUrl("/");
		bean.setFilterChainDefinitions("/ = anon\r\n"//
				+ "                /oauth2Failure.jsp = anon\r\n"//
				+ "                /oauth2-login = oauth2Authc\r\n" //
				+ "                /logout = logout\r\n" //
				+ "                /** = user");

		bean.getFilters().put("oauth2Authc", oauth2AuthenticationFilter);

		return bean;
	}

	// 配置核心安全事务管理器
	@Bean(name = "securityManager")
	public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm) {
		logger.info("--------------shiro已经加载----------------");
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(authRealm);
		return manager;
	}

	// oauth过滤器
	@Bean(name = "oauth2AuthenticationFilter")
	public OAuth2AuthenticationFilter oauth2AuthenticationFilter(@Qualifier("authRealm") AuthRealm authRealm) {
		OAuth2AuthenticationFilter filter = new OAuth2AuthenticationFilter();
		filter.setAuthcCodeParam("code");
		filter.setFailureUrl("/oauth2Failure.jsp");
		return filter;
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

	@Bean
	public AuthRealm authRealm() {
		AuthRealm authRealm = new AuthRealm();

		authRealm.setClientId("c1ebe466-1cdc-4bd3-ab69-77c3561b9dee");
		authRealm.setClientSecret("d8346ea2-6017-43ed-ad68-19c0f971738b");
		authRealm.setAccessTokenUrl("http://localhost:8080/chapter17-server/accessToken");
		authRealm.setUserInfoUrl("http://localhost:8080/chapter17-server/userInfo");
		authRealm.setRedirectUrl("http://localhost:9080/chapter17-client/oauth2-login");

		return authRealm;

	}
}
