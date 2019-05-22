package com.example.rentals.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.Email

@Document
data class Customer(@Id @field:Email val email: String = "someone@example.com", val name: String = "", val contact: Int = 0)