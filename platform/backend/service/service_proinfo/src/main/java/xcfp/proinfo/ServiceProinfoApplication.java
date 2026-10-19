package xcfp.proinfo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Kerr
 * @create 2021-10-13 18:32
 */
@SpringBootApplication
@ComponentScan(basePackages = "xcfp")
//@MapperScan(basePackages = "xcfp.proinfo.mapper")
public class ServiceProinfoApplication {
    public static void main(String[] args){
        SpringApplication.run(ServiceProinfoApplication.class,args);
    }
}
