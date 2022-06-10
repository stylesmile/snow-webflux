package com.snow.framework.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {


	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("*")
			.allowedHeaders("*")
			.allowedMethods("*")
			.allowCredentials(true)
			.maxAge(3600);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 本地文件上传路径
		// registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
		// 	.addResourceLocations("file:" + AppConfig.getProfile() + "/");
		// swagger配置
		// registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/swagger-ui/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
		// 限制上传文件大小
		SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
		partReader.setMaxParts(1);
		partReader.setMaxDiskUsagePerPart(100 * 1024L * 1024L);
		partReader.setEnableLoggingRequestDetails(true);

		MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
		multipartReader.setEnableLoggingRequestDetails(true);
		configurer.defaultCodecs().multipartReader(multipartReader);

		// Enable logging
		// configurer.defaultCodecs().enableLoggingRequestDetails(true);

	}
}