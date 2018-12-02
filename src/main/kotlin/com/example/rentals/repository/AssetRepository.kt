package com.example.rentals.repository

import com.example.rentals.domain.Asset
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import java.util.UUID

interface AssetRepository: ReactiveCassandraRepository<Asset, UUID>