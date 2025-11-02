package miwu.miot.exception

class MiotBusinessException(val code: Int, message: String, cause: Throwable? = null) : MiotException(message, cause) {
    companion object {
        fun loginFailed(code: Int) = MiotBusinessException(code, "Login failed with code: $code")
        fun deviceNotFound(did: String) = MiotBusinessException(404, "Device not found: $did")
        fun sceneExecutionFailed(sceneId: Long) = MiotBusinessException(500, "Failed to execute scene: $sceneId")
    }
}