package org.main.wiredspaceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WiredSpaceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WiredSpaceApiApplication.class, args);
	}

}
