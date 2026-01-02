package miwu.miot.service.body


import com.google.gson.annotations.SerializedName

/**
 * data class GetScene(
 *     @SerializedName("home_owner")
 *     val homeOwner: Long,
 *     @SerializedName("home_id")
 *     val homeId: Long,
 *     @SerializedName("need_recommended_template")
 *     val needRecommendedTemplate: Boolean = false
 * )
 */


data class GetScene(
    @SerializedName("app_version")
    val appVersion: Int = 12,
    @SerializedName("get_type")
    val getType: Int = 2,
    @SerializedName("home_id")
    val homeId: String,
    @SerializedName("owner_uid")
    val ownerUid: String
)