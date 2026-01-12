package miwu.miot.ktx

import kotlinx.serialization.json.Json


val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
