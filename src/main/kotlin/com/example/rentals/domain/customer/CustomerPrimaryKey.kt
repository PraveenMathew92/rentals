package com.example.rentals.domain.customer

import org.springframework.data.cassandra.core.mapping.UserDefinedType
import javax.validation.constraints.Email

@UserDefinedType
data class CustomerPrimaryKey(val tenantId: Int = 0, @field:Email val email: String = "someone@example.com")