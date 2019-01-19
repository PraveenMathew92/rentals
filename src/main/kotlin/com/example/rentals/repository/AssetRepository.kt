package com.example.rentals.repository

import com.example.rentals.domain.asset.AssetPrimaryKey
import com.example.rentals.domain.asset.AssetTable
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface AssetRepository : ReactiveCassandraRepository<AssetTable, AssetPrimaryKey>