package com.dpass.android.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    fun encrypt(plainText: String, secret: ByteArray): ByteArray? {
        /* Encrypt the message. */
        var cipherText: ByteArray? = null
        try {
            val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(secret))
            cipherText = cipher.doFinal(plainText.toByteArray(charset("UTF-8")))
        } catch (e: Exception) {
        }

        return cipherText
    }

    fun decrypt(cipherText: ByteArray, secret: ByteArray): ByteArray? {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        var decryptText: ByteArray? = null
        try {
            val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, generateKey(secret))
            decryptText = cipher.doFinal(cipherText)
        } catch (e: Exception) {
        }

        return decryptText
    }

    private fun generateKey(passphrase: ByteArray): SecretKey? {
        val key: SecretKey
        try {
            val md = MessageDigest.getInstance("SHA-256")// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(passphrase)
            key = SecretKeySpec(md.digest(), "AES")
        } catch (e: NoSuchAlgorithmException) {
            return null
        }

        return key
    }

}