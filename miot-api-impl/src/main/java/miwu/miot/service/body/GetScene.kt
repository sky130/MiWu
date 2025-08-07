package miwu.miot.service.body


import com.google.gson.annotations.SerializedName

data class GetScene(
//    @SerializedName("home_owner")
//    val homeOwner: Long,
    @SerializedName("home_id")
    val homeId: Long,
//    @SerializedName("need_recommended_template")
//    val needRecommendedTemplate: Boolean = false
)