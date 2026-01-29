package com.aaa.gptenniscoach.core

/**
 * Sealed class representing different types of application errors.
 */
sealed class AppError(open val message: String) {
    data class Network(override val message: String) : AppError(message)
    data class Timeout(override val message: String) : AppError(message)
    data class Http(override val message: String, val code: Int) : AppError(message)
    data class Unknown(override val message: String) : AppError(message)
}

