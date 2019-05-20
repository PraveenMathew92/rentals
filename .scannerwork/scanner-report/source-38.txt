package com.example.rentals.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CustomerCannotBeDeletedException : ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Customer has rented out an asset, cannot be deleted")