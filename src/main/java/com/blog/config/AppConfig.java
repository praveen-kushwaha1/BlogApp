package com.blog.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // this class is used to convert object of one class into another class.
    @Bean
    ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
