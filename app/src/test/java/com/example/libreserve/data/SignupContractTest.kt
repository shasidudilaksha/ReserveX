package com.example.libreserve.data

import com.example.libreserve.repository.UserRepository
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class SignupContractTest {
    @Test fun signupUsesAuthFieldsAndDatabaseMetadataNames() {
        val payload = UserRepository.signupPayload("  Test Student  ", "  IT123  ", " student@example.com ", " secret123 ")
        assertEquals("student@example.com", payload.getString("email"))
        assertEquals(" secret123 ", payload.getString("password"))
        val metadata = payload.getJSONObject("data")
        assertEquals("Test Student", metadata.getString("full_name"))
        assertEquals("IT123", metadata.getString("student_id"))
        assertTrue(metadata.getBoolean("terms_accepted"))
        assertFalse(metadata.has("password"))
        assertFalse(metadata.has("email"))
    }

    @Test fun profileUsesDatabaseValuesAndAuthEmail() {
        val id = "10000000-0000-0000-0000-000000000001"
        val auth = JSONObject().put("id", id).put("email", "student@example.com")
            .put("user_metadata", JSONObject().put("full_name", "Stale cached name"))
        val profile = JSONObject().put("user_id", id).put("full_name", "Saved Name").put("student_id", "IT123")
        val user = UserRepository.userFromRows(auth, profile)
        assertEquals(id, user.userId)
        assertEquals("Saved Name", user.fullName)
        assertEquals("IT123", user.studentId)
        assertEquals("student@example.com", user.email)
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsAnotherUsersProfile() {
        UserRepository.userFromRows(JSONObject().put("id", "one"), JSONObject().put("user_id", "two"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankStudentId() {
        UserRepository.signupPayload("Name", " ", "student@example.com", "secret123")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidEmail() {
        UserRepository.signupPayload("Name", "IT123", "invalid@", "secret123")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsShortPassword() {
        UserRepository.signupPayload("Name", "IT123", "student@example.com", "1234567")
    }

}
