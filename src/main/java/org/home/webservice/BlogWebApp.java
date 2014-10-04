package org.home.webservice;

import org.home.data.SpringMongoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan
@EnableAutoConfiguration
@Import(value = { SpringMongoConfig.class })
public class BlogWebApp {

	public static void main(String[] args) {
		SpringApplication.run(BlogWebApp.class, args);
	}
}