package com.example.rentals.domain

import org.springframework.data.annotation.Id
import java.util.*

data class Asset(@Id val id: UUID,
                 val name: String,
                 val category: String)