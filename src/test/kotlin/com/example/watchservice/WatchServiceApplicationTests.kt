package com.example.watchservice

import com.example.watchservice.dto.PersonDto
import com.example.watchservice.repository.PersonRepository
import com.example.watchservice.service.PersonWatchService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.StandardCopyOption


@RunWith(SpringRunner::class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WatchServiceApplicationTests {

    private var id: Int = 0

    private val filename = "temp.json"

    private val testPersons: List<PersonDto> = listOf(
        PersonDto(name = "alex3456", lastName = "pushkin3456"), PersonDto(name = "leva436", lastName = "tolstoi436")
    )

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var personWatchService: PersonWatchService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    class ThreadForWatchService(private val personsWatchService: PersonWatchService) : Runnable {

        override fun run() {
            try {
                personsWatchService.personSearch()
            } catch (ex: InterruptedException) {
                println(Thread.currentThread().name + "has been interrupted!")
            }
        }
    }

    @BeforeEach
    fun init() {
        val fileWriter = FileWriter(System.getProperty("user.dir") + "/${filename}", false)

        fileWriter.write(
            objectMapper.writeValueAsString(testPersons)
        )

        fileWriter.close()
    }


    @AfterEach
    fun destruct() {
        var file = File(System.getProperty("user.dir") + "/${filename}")
        file.delete()

        file = File(System.getProperty("user.dir") + "/persons/${filename}")
        file.delete()
    }

    @Test
    @Order(1)
    fun `1 - WatchService Test`() {
        val thr = Thread(ThreadForWatchService(personWatchService))
        thr.start()
        Files.copy(
            File(System.getProperty("user.dir") + "/${filename}").toPath(),
            File(System.getProperty("user.dir") + "/persons/${filename}").toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        thr.join(400)
        thr.interrupt()

        for (person in testPersons) {
            assertNotEquals(
                personRepository.getByNameAndLastName(
                    objectMapper.readTree(objectMapper.writeValueAsString(person)).get("name").asText(),
                    objectMapper.readTree(objectMapper.writeValueAsString(person)).get("lastName").asText(),
                ), null
            )
        }
    }

    @Test
    @Order(2)
    fun `2 - WatchService Test - Ignoring duplicates`() {
        val thr = Thread(ThreadForWatchService(personWatchService))

        thr.start()
        Files.copy(
            File(System.getProperty("user.dir") + "/${filename}").toPath(),
            File(System.getProperty("user.dir") + "/persons/${filename}").toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        thr.join(400)
        thr.interrupt()

        for (person in testPersons) {
            personRepository.deleteByNameAndLastName(
                objectMapper.readTree(objectMapper.writeValueAsString(person)).get("name").asText(),
                objectMapper.readTree(objectMapper.writeValueAsString(person)).get("lastName").asText()
            )
        }

        for (person in testPersons) {
            assertEquals(
                personRepository.getByNameAndLastName(
                    objectMapper.readTree(objectMapper.writeValueAsString(person)).get("name").asText(),
                    objectMapper.readTree(objectMapper.writeValueAsString(person)).get("lastName").asText(),
                ), null
            )
        }
    }


}
