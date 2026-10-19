package xcfp.proinfo.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kerr
 * @create 2021-10-13 22:05
 */
@Configuration
@MapperScan("xcfp.proinfo.mapper")
public class ProductionInfoConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
