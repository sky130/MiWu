package miwu.miot.exception


class MiotDeviceException(message: String, cause: Throwable? = null) : MiotException(message, cause) {
    companion object {
        fun propertyNotFound(siid: Int, piid: Int) = MiotDeviceException("Property not found: siid=$siid, piid=$piid")
        fun actionFailed(siid: Int, aiid: Int) = MiotDeviceException("Action failed: siid=$siid, aiid=$aiid")
        fun specNotFound(urn: String) = MiotDeviceException("Spec not found for URN: $urn")
        fun modelNotFound(urn: String) = MiotDeviceException("Model not found for URN: $urn")
        fun urnFormatError(urn: String) = MiotDeviceException("Urn format error: $urn")
    }
}