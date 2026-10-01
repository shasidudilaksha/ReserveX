package com.example.libreserve.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionVaultTest {
    @Test fun sessionTokensAreEncryptedAndCanBeCleared() {
        // Test APK context: never reads or changes the installed app's real session.
        val context = InstrumentationRegistry.getInstrumentation().context
        val vault = SessionVault(context)
        try {
            vault.save(JSONObject().put("access_token", "test-access-token").put("refresh_token", "test-refresh-token"))
            val raw = context.getSharedPreferences("supabase_session", 0).getString("session", "")!!
            assertFalse(raw.contains("test-access-token"))
            assertEquals("test-refresh-token", vault.read()!!.getString("refresh_token"))
            vault.clear()
            assertNull(vault.read())
        } finally { vault.clear() }
    }
}
