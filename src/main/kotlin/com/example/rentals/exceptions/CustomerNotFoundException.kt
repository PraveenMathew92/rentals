package com.example.rentals.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CustomerNotFoundException: ResponseStatusException(HttpStatus.NOT_FOUND, "Customer Does Not Exist")