package com.example.watchservice.service

import com.example.watchservice.dto.PersonDto
import com.example.watchservice.repository.PersonRepository
import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.util.*

@Service
class PersonWatchServiceImpl(

    private val watchService: WatchService = FileSystems.getDefault().newWatchService(),

    private val path: Path = Paths.get(System.getProperty("user.dir") + "/persons"),

    ) : PersonWatchService {

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        private val logger = getLogger(PersonWatchService::class.java)
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    override fun personSearch() {
        path.register(
            watchService, StandardWatchEventKinds.ENTRY_CREATE
        )
        while (true) {
            val watchKey: WatchKey = watchService.take()
            for (event in watchKey.pollEvents()) {
                val file = File(path.toString(), event.context().toString())
                lateinit var listPersons: List<PersonDto>

                for (attempt in 1..10) {
                    try {
                        listPersons = objectMapper.readValue(
                            File(path.toString(), event.context().toString()), Array<PersonDto>::class.java
                        ).toList()
                        break
                    } catch (ex: DatabindException) {
                        logger.info("File ${event.context()} has a wrong format")
                        break
                    } catch (ex: IOException) {
                        Thread.sleep(10)
                    }
                }

                for (person in listPersons) {
                    if (personRepository.getByNameAndLastName(
                            person.name, person.lastName
                        ) != null
                    ) logger.info("Ignoring Person{ ${person.name}, ${person.lastName}}")
                    else {
                        personRepository.create(person.name, person.lastName)
                        logger.info("Adding Person{ ${person.name}, ${person.lastName}}")
                    }
                }
            }

            if (!watchKey.reset()) {
                watchKey.cancel()
                watchService.close()
                break
            }
        }
    }
}