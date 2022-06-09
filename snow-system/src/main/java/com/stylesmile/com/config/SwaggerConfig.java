package com.stylesmile.com.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenmiao0212
 * @date 2021/7/2 12:28
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi userApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"com.snow", "com.stylesmile"};
        return GroupedOpenApi.builder().group("snow平台")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("snow平台")
                        .version("1.0")
                        .description("Knife4j")
                        .termsOfService("http://doc.xiaominfo.com")
                        .license(new License().name("Apache 2.0")
                                .url("http://doc.xiaominfo.com")));
    }


//    @Bean
//    public OpenApiCustomiser consumerTypeHeaderOpenAPICustomiser() {
//        return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
//                .forEach(operation -> operation.addParametersItem(new HeaderParameter().$ref("#/components/parameters/myConsumerTypeHeader")));
//    }
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-public")
//                .pathsToMatch("/public/**")
//                .build();
//    }
//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-admin")
//                .pathsToMatch("/admin/**")
//                .addMethodFilter(method -> method.isAnnotationPresent(Admin.class))
//                .build();
//    }


}

