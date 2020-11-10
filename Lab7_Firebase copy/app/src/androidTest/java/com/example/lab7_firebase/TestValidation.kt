package com.example.lab7_firebase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TestValidation {
    // TODO: Test passwords
    // Add two tests for a valid password
    // Add two tests for an invalid password

    @Test
    fun testEmails() {
        val validator = Validators()

        // Add two tests for a valid password
        assertTrue(validator.validPassword("goodjob1"))
        assertTrue(validator.validPassword("1l1l"))

        // Add two tests for an invalid password
        assertFalse(validator.validPassword("mypasswordistoolong"))
        assertFalse(validator.validPassword("nonums"))
    }
}
