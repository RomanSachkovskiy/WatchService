package com.example.watchservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class WatchServiceApplication

fun main(args: Array<String>) {
    runApplication<WatchServiceApplication>(*args)
}
