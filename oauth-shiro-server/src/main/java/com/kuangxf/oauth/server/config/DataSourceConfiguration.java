/*
 * @(#)DBConfig.java        1.0 2018年2月9日
 *
 *
 */

package com.kuangxf.oauth.server.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DataSourceConfiguration {

	@Bean(name = "dataSource")
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(DruidDataSource.class).build();
	}
	
	
	
}
