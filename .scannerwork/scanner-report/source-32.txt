package com.example.rentals.util

import java.util.UUID

fun UUIDorNil(string: String): UUID {
    return try {
        UUID.fromString(string)
    } catch (exception: IllegalArgumentException) {
        UUID(0, 0)
    }
}