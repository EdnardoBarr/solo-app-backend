package ednardo.api.soloapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ednardo.api.soloapp.repository")
public class SoloAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoloAppApplication.class, args);
	}

}
