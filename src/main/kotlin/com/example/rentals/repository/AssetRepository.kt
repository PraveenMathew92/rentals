package com.example.rentals.repository

import com.example.rentals.domain.Asset
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AssetRepository : ReactiveCassandraRepository<Asset, UUID>