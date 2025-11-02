package miwu.miot.exception

class MiotRequestException(message: String, cause: Throwable? = null) : MiotException(message, cause) {
    companion object {
        fun emptyBody() = MiotRequestException("Request body is empty")
        fun invalidParameter(param: String) = MiotRequestException("Invalid parameter: $param")
    }
}