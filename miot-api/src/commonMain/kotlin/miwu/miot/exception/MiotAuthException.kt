package miwu.miot.exception

class MiotAuthException(message: String, cause: Throwable? = null) : MiotException(message, cause) {
    companion object {
        fun tokenMissing(cause: Throwable? = null) =
            MiotAuthException("serviceToken or securityToken not found", cause)

        fun tokenExpired(cause: Throwable? = null) =
            MiotAuthException("Token has expired", cause)

        fun invalidCredentials(cause: Throwable? = null) =
            MiotAuthException("Invalid username or password", cause)
    }
}