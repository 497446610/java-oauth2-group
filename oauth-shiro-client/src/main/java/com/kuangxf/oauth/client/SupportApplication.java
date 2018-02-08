package com.kuangxf.oauth.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SupportApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(SupportApplication.class).web(true).run(args);
	}

}
