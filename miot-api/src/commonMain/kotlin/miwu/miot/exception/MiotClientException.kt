package miwu.miot.exception

class MiotClientException(message: String, cause: Throwable? = null) :
    MiotException(message, cause) {
    companion object {
        fun userNotSet() = MiotClientException("User not set, please call setUser() first")
        fun getUserInfoFailed(cause: Throwable? = null) =
            MiotClientException("Failed to get user info", cause)

        fun getHomesFailed(cause: Throwable? = null) =
            MiotClientException("Failed to get homes", cause)

        fun getDevicesFailed(cause: Throwable? = null) =
            MiotClientException("Failed to get devices", cause)

        fun getDevicesFailed(
            homeOwnerId: Long,
            homeId: Long,
            limit: Int,
            cause: Throwable? = null
        ) = MiotClientException(
            "Failed to get devices, homeOwnerId=$homeOwnerId, homeId=$homeId, limit=$limit",
            cause
        )

        fun getScenesFailed(cause: Throwable? = null) =
            MiotClientException("Failed to get scenes", cause)

        fun runSceneFailed(sceneId: Long, cause: Throwable? = null) =
            MiotClientException("Failed to run scene: $sceneId", cause)

        fun getDeviceAttFailed(did: String, cause: Throwable? = null) =
            MiotClientException("Failed to get device attributes for device: $did", cause)

        fun setDeviceAttFailed(did: String, cause: Throwable? = null) =
            MiotClientException("Failed to set device attributes for device: $did", cause)

        fun actionFailed(
            did: String,
            siid: Int,
            aiid: Int,
            vararg obj: Any,
            cause: Throwable? = null
        ) = MiotClientException(
            "Failed to execute action on device: $did, siid: $siid, aiid: $aiid, in: $obj",
            cause
        )

        fun getSpecAttFailed(specType: String, cause: Throwable? = null) =
            MiotClientException("Failed to get spec attributes for type: $specType", cause)

        fun getIconUrlFailed(model: String, cause: Throwable? = null) =
            MiotClientException("Failed to get icon URL for model: $model", cause)

    }
}