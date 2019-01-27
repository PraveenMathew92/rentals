package com.example.rentals.repository

import com.example.rentals.domain.Asset
import com.example.rentals.domain.AssetPrimaryKey
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AssetRepository : ReactiveCassandraRepository<Asset, AssetPrimaryKey>