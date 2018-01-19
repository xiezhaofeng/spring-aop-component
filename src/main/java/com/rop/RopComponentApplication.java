package com.rop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XZF
 */
@SpringBootApplication
@RestController
public class RopComponentApplication {


	public static void main(String[] args) {
		SpringApplication.run(RopComponentApplication.class, args);
	}

}
