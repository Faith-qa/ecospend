package com.ecospend.api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue
@SpringBootTest
class EcospendApiApplicationTests {

	@Test
	fun contextLoads() {
	}
	@Test
	fun additionWorks(){
		assertTrue(1 + 1 == 2)
	}

}
