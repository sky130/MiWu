package miwu.miot.model.att


import com.google.gson.annotations.SerializedName
import kotlin.collections.iterator

data class SpecAtt(
    @SerializedName("description") val description: String,
    @SerializedName("services") val services: List<Service>,
    @SerializedName("type") val type: String
) {
    lateinit var descriptionTranslation: String

    data class Service(
        @SerializedName("actions") val actions: List<Action>?,
        @SerializedName("description") val description: String,
        @SerializedName("iid") val iid: Int,
        @SerializedName("properties") val properties: List<Property>?,
        @SerializedName("type") val type: String
    ) {
        lateinit var descriptionTranslation: String

        data class Action(
            @SerializedName("description") val description: String,
            @SerializedName("iid") val iid: Int,
            @SerializedName("in") val `in`: List<Any>,
            @SerializedName("out") val `out`: List<Any>,
            @SerializedName("type") val type: String
        ) {
            lateinit var descriptionTranslation: String
        }

        data class Property(
            @SerializedName("access") val access: List<String>,
            @SerializedName("description") val description: String,
            @SerializedName("format") val format: String,
            @SerializedName("gatt-access") val gattAccess: List<Any>?,
            @SerializedName("iid") val iid: Int,
            @SerializedName("source") val source: Int,
            @SerializedName("type") val type: String,
            @SerializedName("unit") val unit: String?,
            @SerializedName("value-list") val valueList: List<Value>?,
            @SerializedName("value-range") val valueRange: List<Number>?
        ) {
            lateinit var descriptionTranslation: String

            data class Value(
                @SerializedName("description") val description: String,
                @SerializedName("value") val value: Int
            ) {
                lateinit var descriptionTranslation: String
            }
        }
    }

    fun initVariable(){
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