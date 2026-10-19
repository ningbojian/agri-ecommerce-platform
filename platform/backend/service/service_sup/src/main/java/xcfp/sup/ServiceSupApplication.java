package xcfp.sup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Kerr
 * @create 2021-10-17 0:28
 */
@SpringBootApplication
@ComponentScan(basePackages = "xcfp")
public class ServiceSupApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSupApplication.class,args);
    }
}
