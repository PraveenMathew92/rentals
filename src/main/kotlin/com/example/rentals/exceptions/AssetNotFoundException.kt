package com.example.rentals.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AssetNotFoundException : ResponseStatusException(HttpStatus.NOT_FOUND, "asset does not exist")