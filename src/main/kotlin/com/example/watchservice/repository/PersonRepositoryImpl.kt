package com.example.watchservice.repository

import com.example.watchservice.model.PersonModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class PersonRepositoryImpl(

    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val jdbcTemplateForCreateTable: JdbcTemplate

) : PersonRepository {

    @PostConstruct
    fun init() {
        jdbcTemplateForCreateTable.execute(
            "create table if not exists persons(" +
                    "id serial primary key, " +
                    "name varchar(255)," +
                    " last_name varchar(255)" +
                    ")"
        )
    }

    override fun getByNameAndLastName(name: String, lastName: String): PersonModel? =
        jdbcTemplate.query(
            "select * from persons where name = :name and last_name = :lastName",
            mapOf(
                "name" to name,
                "lastName" to lastName
            ),
            ROW_MAPPER
        ).firstOrNull()

    override fun deleteByNameAndLastName(name: String, lastName: String) {
        jdbcTemplate.update(
            "delete from persons where name = :name and last_name = :lastName", mapOf(
                "name" to name,
                "lastName" to lastName
            )
        )
    }

    override fun create(name: String, lastName: String) {
        jdbcTemplate.update(
            "insert into persons (name, last_name) values(:name, :lastName)",
            MapSqlParameterSource(
                mapOf(
                    "name" to name,
                    "lastName" to lastName,
                )
            )
        )
    }

    private companion object {
        val ROW_MAPPER = RowMapper<PersonModel> { rs, _ ->
            PersonModel(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                lastName = rs.getString("last_name")
            )
        }
    }

}