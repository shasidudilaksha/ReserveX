package com.example.libreserve.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Auth tokens are encrypted with a non-exportable Android Keystore key. */
class SessionVault(context: Context) {
    private val prefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

    fun read(): JSONObject? = try {
        prefs.getString("session", null)?.let { encoded ->
            val parts = encoded.split(":")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, decode(parts[0])))
            JSONObject(String(cipher.doFinal(decode(parts[1])), Charsets.UTF_8))
        }
    } catch (_: Exception) {
        clear()
        null
    }

    fun save(session: JSONObject) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val encrypted = cipher.doFinal(session.toString().toByteArray(Charsets.UTF_8))
        check(prefs.edit().putString("session", "${encode(cipher.iv)}:${encode(encrypted)}").commit()) {
            "Could not save your session. Please sign in again."
        }
    }

    fun clear() { prefs.edit().clear().commit() }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }

    private fun encode(bytes: ByteArray) = Base64.encodeToString(bytes, Base64.NO_WRAP)
    private fun decode(value: String) = Base64.decode(value, Base64.NO_WRAP)
    private companion object { const val ALIAS = "reservex_supabase_session" }
}
