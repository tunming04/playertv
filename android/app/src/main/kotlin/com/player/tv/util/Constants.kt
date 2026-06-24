package com.player.tv.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object Constants {
    // Cloudflare Worker URL - deployed
    const val API_BASE_URL = "https://playertv-app.buoisangvatoi.workers.dev"
    
    // App secret - phải khá»›p vá»›i APP_SECRET trong worker
    private const val APP_SECRET = "app-secret-key-change-me"
    
    // Token cache
    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0
    
    /**
     * Get valid app token (tá»± refresh khi hết hạn)
     */
    suspend fun getAppToken(): String {
        val now = System.currentTimeMillis()
        if (cachedToken != null && now < tokenExpiry) {
            return cachedToken!!
        }
        
        // Generate token locally (không cần call API)
        val hour = now / 3600000
        val token = generateToken(APP_SECRET, hour)
        cachedToken = token
        tokenExpiry = (hour + 1) * 3600000 // Hết giá» sau
        
        return token
    }
    
    /**
     * Generate time-based token (same algorithm as worker)
     */
    private fun generateToken(secret: String, hour: Long): String {
        val message = "$secret:$hour"
        val bytes = message.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Get playlist URL with token
     */
    suspend fun getPlaylistUrl(): String {
        val token = getAppToken()
        return "$API_BASE_URL/api/playlist?token=$token"
    }
    
    /**
     * Decode obfuscated URL tá»« worker
     */
    fun decodeUrl(encoded: String): String {
        return try {
            String(Base64.decode(encoded, Base64.DEFAULT))
        } catch (e: Exception) {
            encoded // Return original nếu decode fail
        }
    }
    
    /**
     * Encrypt URL for local storage
     */
    fun encryptUrl(url: String): String {
        return try {
            val key = SecretKeySpec(APP_SECRET.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            Base64.encodeToString(cipher.doFinal(url.toByteArray()), Base64.DEFAULT)
        } catch (e: Exception) {
            url
        }
    }
    
    /**
     * Decrypt URL from local storage
     */
    fun decryptUrl(encrypted: String): String {
        return try {
            val key = SecretKeySpec(APP_SECRET.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, key)
            String(cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT)))
        } catch (e: Exception) {
            encrypted
        }
    }
    
    /**
     * Decode encrypted default URL
     */
    private fun d(encoded: String): String {
        return try {
            String(Base64.decode(encoded, Base64.DEFAULT))
        } catch (e: Exception) {
            ""
        }
    }
    
}
