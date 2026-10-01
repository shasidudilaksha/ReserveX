package com.example.libreserve.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Opt-in live smoke test: configure a project and run the SQL migration first. */
@RunWith(AndroidJUnit4::class)
class SupabaseConnectionTest {
    @Test
    fun readsLibrariesUsingPublishableKey() = runBlocking {
        assumeTrue("No Supabase project configured", SupabaseClient.isConfigured)
        SupabaseClient.checkConnection()
    }
}
