package xcfp.common.config;

import com.google.common.base.Predicates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 分组：小程序端 /app、管理端 /admin、服务间 /internal。
 * 如 Springfox 与当前 Spring Boot 版本不兼容，可设置 xcfp.swagger.enabled=false 关闭。
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "xcfp.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class Swagger2Config {

    @Bean
    public Docket appApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("appApi")
                .apiInfo(apiInfo("小程序端 API"))
                .select()
                .paths(Predicates.and(PathSelectors.regex("/app/.*")))
                .build();
    }

    @Bean
    public Docket adminApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(apiInfo("管理后台 API"))
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    @Bean
    public Docket internalApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("internalApi")
                .apiInfo(apiInfo("服务间内部 API"))
                .select()
                .paths(Predicates.and(PathSelectors.regex("/internal/.*")))
                .build();
    }

    private ApiInfo apiInfo(String title) {
        return new ApiInfoBuilder()
                .title(title)
                .description("xcfp 电商助农平台接口文档")
                .version("2.0")
                .build();
    }
}
