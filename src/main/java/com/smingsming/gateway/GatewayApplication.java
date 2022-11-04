package com.smingsming.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableEurekaClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedOrigin("http://3.35.94.116:3000");
//		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.setAllowCredentials(true);
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

//	@Order(-1)
//		corsConfiguration.addAllowedOrigin("http://3.35.94.116:3000");
//		corsConfiguration.addAllowedOrigin("http://3.35.94.116:3000");
//		corsConfiguration.addAllowedOrigin("http://localhost:3000");

	@Bean
	public CorsWebFilter corsWebFilter() {
		return new CorsWebFilter(corsConfigurationSource());
	}


}
