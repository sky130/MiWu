package miot.kotlin.model.att


import com.google.gson.annotations.SerializedName

data class SpecAtt(
    @SerializedName("description") val description: String,
    @SerializedName("services") val services: List<Service>,
    @SerializedName("type") val type: String
) {
    data class Service(
        @SerializedName("actions") val actions: List<Action>?,
        @SerializedName("description") var description: String,
        @SerializedName("iid") val iid: Int,
        @SerializedName("properties") val properties: List<Property>?,
        @SerializedName("type") val type: String
    ) {
        data class Action(
            @SerializedName("description") var description: String,
            @SerializedName("iid") val iid: Int,
            @SerializedName("in") val inX: List<Any>,
            @SerializedName("out") val `out`: List<Any>,
            @SerializedName("type") val type: String
        )

        data class Property(
            @SerializedName("access") val access: List<String>,
            @SerializedName("description") var description: String,
            @SerializedName("format") val format: String,
            @SerializedName("gatt-access") val gattAccess: List<Any>?,
            @SerializedName("iid") val iid: Int,
            @SerializedName("source") val source: Int,
            @SerializedName("type") val type: String,
            @SerializedName("unit") val unit: String,
            @SerializedName("value-list") val valueList: List<Value>?,
            @SerializedName("value-range") val valueRange: List<Number>?
        ) {
            data class Value(
                @SerializedName("description") var description: String, @SerializedName("value") val value: Int
            )
        }
    }
}