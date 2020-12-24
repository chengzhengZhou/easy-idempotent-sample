package cn.carbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 示例代码
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月24日
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(new Class<?>[] { Application.class });
        application.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = application.run(args);
    }
}
