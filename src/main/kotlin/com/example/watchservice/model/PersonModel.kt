package com.example.watchservice.model

import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.Table
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
@Table(name = "persons")
data class PersonModel(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "name", length = 255)
    val name: String = "",

    @Column(name = "lastName", length = 255)
    val lastName: String = ""
)