package com.example.rentals.domain.asset

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table
data class AssetTable(@PrimaryKey val primaryKey: AssetPrimaryKey, val asset: Asset)