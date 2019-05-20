package com.example.rentals.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AssetCannotBeRentedException : ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "The asset is already rented")
