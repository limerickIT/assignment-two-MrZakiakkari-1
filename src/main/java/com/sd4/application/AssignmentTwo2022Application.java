package com.sd4.application;

import java.awt.image.BufferedImage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

@SpringBootApplication
@ComponentScan(
		{
			"com.sd4.service", "com.sd4.controller"
		})
@EntityScan("com.sd4.model")
@EnableJpaRepositories("com.sd4.repository")
public class AssignmentTwo2022Application
{

	public static void main(String[] args)
	{
		SpringApplication.run(AssignmentTwo2022Application.class, args);
	}
	/**
	 * Also, we need to manually register a message converter for BufferedImage
	 * HTTP Responses because there is no default:
	 *
	 * @@link https://www.baeldung.com/java-generating-barcodes-qr-codes
	 * @return
	 */
	@Bean
	public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter()
	{
		return new BufferedImageHttpMessageConverter();
	}
}
