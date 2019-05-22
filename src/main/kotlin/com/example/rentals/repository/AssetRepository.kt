package com.example.rentals.repository

import com.example.rentals.domain.Asset
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AssetRepository : ReactiveMongoRepository<Asset, UUID>