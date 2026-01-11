package miwu.miot.exception


class MiotDeviceException(message: String, cause: Throwable? = null) :
    MiotException(message, cause) {
    companion object {
        fun propertyNotFound(siid: Int, piid: Int, cause: Throwable? = null) =
            MiotDeviceException("Property not found: siid=$siid, piid=$piid", cause)

        fun actionFailed(siid: Int, aiid: Int, cause: Throwable? = null) =
            MiotDeviceException("Action failed: siid=$siid, aiid=$aiid", cause)

        fun specNotFound(urn: String, cause: Throwable? = null) =
            MiotDeviceException("Spec not found for URN: $urn", cause)

        fun modelNotFound(urn: String, cause: Throwable? = null) =
            MiotDeviceException("Model not found for URN: $urn", cause)

        fun urnFormatError(urn: String, cause: Throwable? = null) =
            MiotDeviceException("Urn format error: $urn", cause)
    }
}