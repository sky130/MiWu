package miot.kotlin.utils

fun String.parseUrn() = Urn.parseFrom(this)

data class Urn(
    val namespace: String,
    val type: String,
    val name: String,
    val value: String,
    val vendorProduct: String?,
    val version: String?,
) {

    override fun toString(): String {
        return arrayOf(
            namespace,
            type,
            name,
            value,
            vendorProduct ?: "",
            version ?: ""
        ).filter { it.isNotEmpty() }
            .joinToString { ":" }
    }


    companion object {

        private val validType = setOf(
            "template",
            "property",
            "action",
            "event",
            "service",
            "device",
        )

        /**
         * <URN> ::= "urn:"<namespace>":"<type>":"<name>":"<value>[":"<vendor-product>":"<version>]
         * urn:miot-spec-v2:service:device-information:00007801
         */

        fun parseFrom(str: String) = str.split(":").let { parts ->
            if (parts.size < 5 || parts[0] != "urn") {
                throw IllegalArgumentException("Invalid URN string")
            }
            val namespace = parts[1]
            val type = parts[2]
            if (type !in validType) {
                throw IllegalArgumentException("Invalid type of urn")
            }
            val name = parts[3]
            val value = parts[4]
            val vendorProduct = if (parts.size > 5) parts[5] else null
            val version = if (parts.size > 6) parts[6] else null
            Urn(namespace, type, name, value, vendorProduct, version)
        }
    }
}
