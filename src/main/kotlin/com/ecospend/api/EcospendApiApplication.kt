package com.ecospend.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EcospendApiApplication

fun main(args: Array<String>) {
	runApplication<EcospendApiApplication>(*args)
}
