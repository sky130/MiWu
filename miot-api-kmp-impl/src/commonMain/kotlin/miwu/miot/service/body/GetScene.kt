package miwu.miot.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @Serializable data class GetScene(
 *     @SerialName("home_owner")
 *     val homeOwner: Long,
 *     @SerialName("home_id")
 *     val homeId: Long,
 *     @SerialName("need_recommended_template")
 *     val needRecommendedTemplate: Boolean = false
 * )
 */


@Serializable
data class GetScene(
    @SerialName("app_version")
    var appVersion: Int = 12,
    @SerialName("get_type")
    var getType: Int = 2,
    @SerialName("home_id")
    var homeId: String,
    @SerialName("owner_uid")
    var ownerUid: String
)