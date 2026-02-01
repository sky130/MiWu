package miwu.support.urn

/**
 * MIoT（小米物联网）规范的 URN（统一资源名称）表示类
 *
 * URN 表达式遵循 URN 语法规范 (RFC2141), 6个字段, 最后一个字段为可选:
 *
 * ```
 * <URN> ::= "urn:"<namespace>":"<type>":"<name>":"<value>[":"<vendor-product>":"<version>]
 * ```
 *
 * 示例:
 * - `urn:miot-spec-v2:service:device-information:00007801`
 * - `urn:miot-spec-v2:property:on:00000001:light:00000001`
 *
 * 根据MIoT规范, URN用于唯一标识设备、服务、属性、动作、事件等资源
 *
 * @param namespace 命名空间, 小米定义的规范为miot-spec-v2, 蓝牙联盟定义的规范为bluetooth-spec
 * @param type 资源类型, 必须是预定义[validType]的有效类型之一
 * @param name 资源名称, SpecificationType, 描述资源功能的英文名称, 如"device-information"
 * @param value 资源值, 16进制字符串, 使用UUID前8个字符
 * @param vendorProduct 厂商产品标识, 可选, 厂家 + 产品代号, 有意义的单词或单词组合(小写字母)
 * @param version 版本号, 可选, 只能是数字
 */
data class Urn(
    val namespace: String,
    val type: String,
    val name: String,
    val value: String,
    val vendorProduct: String?,
    val version: Int?,
) {

    override fun toString() =
        arrayOf(
            namespace,
            type,
            name,
            value,
            vendorProduct ?: "",
            version.toString()
        ).filter(String::isNotEmpty).joinToString { ":" }

    companion object {
        /** 有效的资源类型集合, 定义 MIoT 规范支持的所有资源类型 */
        private val validType = setOf(
            "template",
            "property",
            "action",
            "event",
            "service",
            "device",
        )

        /**
         * 解析URN字符串为Urn对象
         *
         * @param str 待解析的 URN 字符串, 必须符合 MIoT URN 格式
         * @return 解析后的Urn对象
         * @throws IllegalArgumentException 当字符串格式无效或类型不合法时抛出异常
         *
         * 示例：
         * ```
         * val urn = Urn.parseFrom("urn:miot-spec-v2:service:device-information:00007801")
         * ```
         */
        fun parseFrom(str: String) = str.split(":").let { parts ->
            if (parts.size < 5 || parts[0] != "urn") error("Invalid URN string")
            val namespace = parts[1]
            val type = parts[2]
            if (type !in validType) error("Invalid type of urn")
            val name = parts[3]
            val value = parts[4]
            val vendorProduct = if (parts.size > 5) parts[5] else null
            val version = if (parts.size > 6) parts[6] else null
            Urn(namespace, type, name, value, vendorProduct, version?.toInt())
        }
    }
}