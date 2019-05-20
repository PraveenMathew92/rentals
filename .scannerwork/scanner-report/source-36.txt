package com.example.rentals.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AssetCannotBeDeletedException : ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Asset id rented out, cannot be deleted")
