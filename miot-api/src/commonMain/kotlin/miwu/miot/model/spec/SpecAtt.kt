package miwu.miot.model.spec

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import miwu.miot.model.JsonAnySerializer
import miwu.support.urn.Urn
import kotlin.collections.iterator

typealias SpecService = SpecAtt.Service
typealias SpecAction = SpecAtt.Action
typealias SpecProperty = SpecAtt.Property

@Serializable
data class SpecAtt(
    @SerialName("description") val description: String,
    @SerialName("services") val services: List<Service>,
    @SerialName("type") val type: String
) {
    @Transient
    var descriptionTranslation: String = ""

    @Serializable
    data class Service(
        @SerialName("actions") val actions: List<Action>? = null,
        @SerialName("description") val description: String,
        @SerialName("iid") val iid: Int,
        @SerialName("properties") val properties: List<Property>? = null,
        @SerialName("type") val type: String
    ) {
        @Transient
        var descriptionTranslation: String = ""

        val name: String by lazy { Urn.parseFrom(type).name }
    }


    /**
     * @param valueRange 0: min, 1: max, 2: step
     */
    @Serializable
    data class Property(
        @SerialName("access") val access: List<String>,
        @SerialName("description") val description: String,
        @SerialName("format") val format: String,
        @SerialName("gatt-access") val gattAccess: List<@Serializable(with = JsonAnySerializer::class) Any>? = null,
        @SerialName("iid") val iid: Int,
        @SerialName("source") val source: Int? = null,
        @SerialName("type") val type: String,
        @SerialName("unit") val unit: String? = null,
        @SerialName("value-list") val valueList: List<Value>? = null,
        @SerialName("value-range") val valueRange: List<@Serializable(with = JsonAnySerializer::class) Any>? = null
    ) {
        @Transient
        var descriptionTranslation: String = ""

        val name: String by lazy { Urn.parseFrom(type).name }

        /**
         * bool	    布尔值: true/false
         * uint8	无符号8位整型
         * uint16	无符号16位整型
         * uint32	无符号32位整型
         * int8	    有符号8位整型
         * int16	有符号16位整型
         * int32	有符号32位整型
         * int64	有符号64位整型
         * float	浮点数
         * string	字符串
         */
        fun getDefaultValue(): Any {
            valueList?.firstOrNull()?.value?.let { return it }
            val rangeMin = valueRange
                ?.takeIf { it.size >= 3 && it.all { item -> item is Number } }
                ?.firstOrNull() as? Number
            return when (format) {
                "bool" -> false
                "string" -> ""
                "uint8", "uint16", "int8", "int16", "int32" -> rangeMin?.toInt() ?: 0
                "uint32", "int64" -> rangeMin?.toLong() ?: 0L
                "float" -> rangeMin?.toFloat() ?: 0f
                else -> rangeMin ?: 0
            }
        }

        fun firstValueOrNull(predicate: (String) -> Boolean): Value? {
            return valueList?.firstOrNull { predicate(it.description) }
        }

        @Serializable
        data class Value(
            @SerialName("description") val description: String,
            @SerialName("value") val value: Int
        ) {
            @Transient
            var descriptionTranslation: String = ""
        }
    }

    @Serializable
    data class Action(
        @SerialName("description") val description: String,
        @SerialName("iid") val iid: Int,
        @SerialName("in") val `in`: List<@Serializable(with = JsonAnySerializer::class) Any>,
        @SerialName("out") val `out`: List<@Serializable(with = JsonAnySerializer::class) Any>,
        @SerialName("type") val type: String
    ) {
        @Transient
        var descriptionTranslation: String = ""

        val name: String by lazy { Urn.parseFrom(type).name }
    }

    fun initVariable() {
        // 这一段用于处理 Kotlin 中变量无法正常赋值的问题. 重新手动赋值才不会 null
        descriptionTranslation = description
        services.forEach { service ->
            service.properties?.forEach { property ->
                property.valueList?.forEach { value ->
                    value.descriptionTranslation = value.description
                }
                property.descriptionTranslation = property.description
            }

            service.actions?.forEach { action ->
                action.descriptionTranslation = action.description
            }

            service.descriptionTranslation = service.description
        }
    }

    fun convertLanguage(language: Map<String, String>): SpecAtt {
        for ((id, desc) in language) {
            val splitId = id.split(":")
            when (splitId.size / 2) {
                LANG_SERVICE -> {
                    val siid = splitId[1].toInt()
                    services.firstOrNull { it.iid == siid }
                        ?.descriptionTranslation = desc
                }

                LANG_PROPERTY_OR_ACTION -> {
                    val siid = splitId[1].toInt()
                    val piid = splitId[3].toInt()
                    val type = splitId[2]
                    services.firstOrNull { it.iid == siid }?.let { service ->
                        when (type) {
                            "property" -> {
                                service.properties
                                    ?.firstOrNull { it.iid == piid }
                                    ?.descriptionTranslation = desc
                            }

                            "action" -> {
                                service.actions
                                    ?.firstOrNull { it.iid == piid }
                                    ?.descriptionTranslation = desc
                            }
                        }
                    }
                }

                LANG_VALUE_LIST -> {
                    val siid = splitId[1].toInt()
                    val piid = splitId[3].toInt()
                    val index = splitId[5].toInt()
                    services.firstOrNull { it.iid == siid }
                        ?.properties?.firstOrNull { it.iid == piid }
                        ?.valueList?.get(index)
                        ?.descriptionTranslation = desc
                }
            }
        }
        return this
    }

    companion object {
        private const val LANG_SERVICE = 1
        private const val LANG_PROPERTY_OR_ACTION = 2
        private const val LANG_VALUE_LIST = 3
    }

}
