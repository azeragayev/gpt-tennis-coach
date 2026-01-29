package com.aaa.gptenniscoach.core

import kotlinx.serialization.json.Json

object Jsons {
    /**
     * JSON serializer configured for lenient parsing and API compatibility.
     */
    val lenient: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }
}

