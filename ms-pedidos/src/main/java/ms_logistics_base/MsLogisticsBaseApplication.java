package ms_logistics_base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ms_logistics_base", "ms_logistics_base.controller", "ms_logistics_base.service", "ms_logistics_base.repository", "ms_logistics_base.config"})
public class MsLogisticsBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsLogisticsBaseApplication.class, args);
	}
}
