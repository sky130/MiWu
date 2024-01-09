package miot.kotlin.utils

fun String.parseUrn(): Urn {
    /**
     * 使用Kotlin写一个解析工具
     * <URN> ::= "urn:"<namespace>":"<type>":"<name>":"<value>[":"<vendor-product>":"<version>]
     * urn:miot-spec-v2:service:device-information:00007801
     */
    split(":").also { parts ->
        if (parts.size < 5 || parts[0] != "urn") {
            throw IllegalArgumentException("Invalid URN string")
        }
        val namespace = parts[1]
        val type = parts[2]
        val name = parts[3]
        val value = parts[4]
        val vendorProduct = if (parts.size > 5) parts[5] else ""
        val version = if (parts.size > 6) parts[6] else ""
        return Urn(namespace, type, name, value, vendorProduct, version)
    }
}

data class Urn(
    val namespace: String,
    val type: String,
    val name: String,
    val value: String,
    val vendorProduct: String = "",
    val version: String = ""
) {
    override fun toString(): String {
        return arrayOf(namespace, type, name, value, vendorProduct, version).filter { it.isNotEmpty() }
            .joinToString { ":" }
    }
}
