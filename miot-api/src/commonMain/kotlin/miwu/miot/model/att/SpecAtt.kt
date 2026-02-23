package miwu.miot.model.att

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import miwu.miot.model.JsonAnySerializer
import kotlin.collections.iterator

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
        }

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

            @Serializable
            data class Value(
                @SerialName("description") val description: String,
                @SerialName("value") val value: Int
            ) {
                @Transient
                var descriptionTranslation: String = ""
            }
        }
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
                1 -> { // service
                    val siid = splitId[1].toInt()
                    services.firstOrNull { it.iid == siid }
                        ?.descriptionTranslation = desc
                }

                2 -> { // property, action
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

                3 -> { // value-list
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

}