package xcfp.proinfo.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xcfp.common.auth.InternalTokenInterceptor;

/**
 * @author Kerr
 * @create 2021-10-13 22:05
 */
@Configuration
@MapperScan("xcfp.proinfo.mapper")
public class ProductionInfoConfig implements WebMvcConfigurer {

    @Autowired
    private InternalTokenInterceptor internalTokenInterceptor;

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalTokenInterceptor).addPathPatterns("/internal/**");
    }
}
