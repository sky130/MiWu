package miwu.miot.exception

sealed class MiotNetworkException(message: String, cause: Throwable? = null) :
    MiotException(message, cause)

class MiotConnectionException(message: String, cause: Throwable? = null) : MiotNetworkException(message, cause)

class MiotTimeoutException(message: String, cause: Throwable? = null) : MiotNetworkException(message, cause)

class MiotHttpException(message: String, cause: Throwable? = null) : MiotNetworkException(message, cause)
