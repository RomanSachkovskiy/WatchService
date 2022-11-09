package com.example.watchservice.repository

import com.example.watchservice.model.PersonModel

interface PersonRepository {

    fun getByNameAndLastName(name: String, lastName: String): PersonModel?

    fun create(name: String, lastName: String)

    fun deleteByNameAndLastName(name: String, lastName: String)

}