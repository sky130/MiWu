package miwu.miot.exception

class MiotParseException(message: String, cause: Throwable? = null) : MiotException(message, cause) {
    companion object {
        fun jsonParse(cause: Throwable) = MiotParseException("Failed to parse JSON response", cause)
        fun typeMismatch(expected: String, actual: String) = MiotParseException("Type mismatch: expected $expected, got $actual")
    }
}