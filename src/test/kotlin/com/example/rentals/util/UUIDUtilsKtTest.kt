package com.example.rentals.util

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.UUID

internal class UUIDUtilsKtTest {
    @Test
    fun `should return a UUID for a vaild UUID string`() {
        val validUUID = "65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"
        assertEquals(UUID.fromString(validUUID), UUIDorNil(validUUID))
    }

    @Test
    fun `should return a nil UUID for an invaild UUID string`() {
        val invalidUUID = "NotValid UUID"
        assertEquals(UUID(0, 0), UUIDorNil(invalidUUID))
    }
}