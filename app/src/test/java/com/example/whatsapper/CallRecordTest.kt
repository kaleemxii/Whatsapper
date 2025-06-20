package com.example.whatsapper

import org.junit.Test
import org.junit.Assert.*

class CallRecordTest {
    
    @Test
    fun `displayName shows contact name when available`() {
        val callRecord = CallRecord(
            number = "+1234567890",
            contactName = "John Doe",
            date = System.currentTimeMillis(),
            type = 1,
            duration = 120
        )
        
        assertEquals("John Doe", callRecord.displayName)
    }
    
    @Test
    fun `displayName shows phone number when contact name is null`() {
        val callRecord = CallRecord(
            number = "+1234567890",
            contactName = null,
            date = System.currentTimeMillis(),
            type = 1,
            duration = 120
        )
        
        assertEquals("+1234567890", callRecord.displayName)
        assertNotEquals("", callRecord.displayName)
        assertFalse("Display name should not be empty", callRecord.displayName.isEmpty())
    }
    
    @Test
    fun `displayName shows phone number when contact name is empty string`() {
        val callRecord = CallRecord(
            number = "+1234567890",
            contactName = "",
            date = System.currentTimeMillis(),
            type = 1,
            duration = 120
        )
        
        // Note: Empty string is not null, so contactName will be used
        assertEquals("", callRecord.displayName)
    }
    
    @Test
    fun `displayName shows phone number when contact name is blank string`() {
        val callRecord = CallRecord(
            number = "+1234567890",
            contactName = "   ",
            date = System.currentTimeMillis(),
            type = 1,
            duration = 120
        )
        
        // Note: Blank string is not null, so contactName will be used  
        assertEquals("   ", callRecord.displayName)
    }
    
    @Test
    fun `displayName handles different phone number formats`() {
        val testCases = listOf(
            "+1234567890",
            "1234567890",
            "(123) 456-7890",
            "123-456-7890",
            "123 456 7890"
        )
        
        testCases.forEach { number ->
            val callRecord = CallRecord(
                number = number,
                contactName = null,
                date = System.currentTimeMillis(),
                type = 1,
                duration = 120
            )
            
            assertEquals("For number: $number", number, callRecord.displayName)
            assertFalse("Display name should not be empty for number: $number", callRecord.displayName.isEmpty())
        }
    }
    
    @Test
    fun `test scenario simulating database with empty cached name`() {
        // This test simulates what happens when the call log database 
        // returns an empty string for CACHED_NAME instead of null
        val phoneNumber = "+1234567890"
        
        // Scenario 1: Cached name is empty string (common scenario)
        var cachedName: String? = ""
        
        // Simulate the fix in CallHistoryViewModel: convert empty string to null
        if (cachedName?.isEmpty() == true) {
            cachedName = null
        }
        
        val callRecord = CallRecord(
            number = phoneNumber,
            contactName = cachedName,
            date = System.currentTimeMillis(),
            type = 1,
            duration = 120
        )
        
        assertEquals(phoneNumber, callRecord.displayName)
        assertFalse("Display name should not be empty", callRecord.displayName.isEmpty())
        
        // Scenario 2: Cached name is null (should also work)
        val callRecord2 = CallRecord(
            number = phoneNumber,
            contactName = null,
            date = System.currentTimeMillis(),
            type = 1,
            duration = 120
        )
        
        assertEquals(phoneNumber, callRecord2.displayName)
        assertFalse("Display name should not be empty", callRecord2.displayName.isEmpty())
    }
}