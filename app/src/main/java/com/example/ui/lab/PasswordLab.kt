package com.example.ui.lab

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordLab {

    fun analyze(password: String): PasswordAnalysis {
        if (password.isEmpty()) {
            return PasswordAnalysis(
                score = 0,
                label = "Enter a lab password",
                suggestions = listOf("Use a dummy password for this lab.")
            )
        }

        var score = 0
        val suggestions = mutableListOf<String>()

        if (password.length >= 8) score += 25
        else suggestions += "Use at least 8 characters."

        if (password.length >= 12) score += 15

        if (password.any { it.isUpperCase() }) score += 15
        else suggestions += "Add uppercase letters."

        if (password.any { it.isLowerCase() }) score += 10
        else suggestions += "Add lowercase letters."

        if (password.any { it.isDigit() }) score += 15
        else suggestions += "Add numbers."

        if (password.any { !it.isLetterOrDigit() }) score += 20
        else suggestions += "Add a symbol."

        val common = setOf(
            "123456",
            "password",
            "12345678",
            "qwerty",
            "admin",
            "123456789",
            "welcome",
            "letmein",
            "iloveyou",
            "monkey"
        )

        if (password.lowercase() in common) {
            score = minOf(score, 15)
            suggestions += "This is a commonly used password (found in top rockyou dictionaries)."
        }

        val label = when {
            score < 30 -> "Very Weak"
            score < 50 -> "Weak"
            score < 70 -> "Medium"
            score < 90 -> "Strong"
            else -> "Very Strong"
        }

        return PasswordAnalysis(
            score = score.coerceIn(0, 100),
            label = label,
            suggestions = suggestions
        )
    }

    fun createSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun sha256(password: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = salt + password.toByteArray(Charsets.UTF_8)
        return digest.digest(input)
            .joinToString("") { "%02x".format(it) }
    }

    fun md5(password: String): String {
        val digest = MessageDigest.getInstance("MD5")
        return digest.digest(password.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateDummyPassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%&*"
        val random = SecureRandom()
        val length = 14
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
}
