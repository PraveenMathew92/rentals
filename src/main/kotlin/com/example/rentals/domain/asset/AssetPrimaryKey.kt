package com.example.rentals.domain.asset

import org.springframework.data.cassandra.core.mapping.UserDefinedType
import java.util.UUID

@UserDefinedType
data class AssetPrimaryKey(val tenantId: Int = 0, val assetId: UUID = UUID(0,0))