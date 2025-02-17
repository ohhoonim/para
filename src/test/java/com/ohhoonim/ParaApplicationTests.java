package com.ohhoonim;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;


@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ParaApplicationTests {

	Logger log = LoggerFactory.getLogger(ParaApplicationTests.class);

	@Autowired
	PostgreSQLContainer<?> container;

	@Test
	void contextLoads() {
		container.start();
		log.info("----{}", container.getJdbcUrl());
		log.info("----{}", container.getUsername());
		log.info("----{}", container.getPassword());
		container.stop();
	}

}
