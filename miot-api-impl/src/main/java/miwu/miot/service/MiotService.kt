package miwu.miot.service

import miwu.miot.model.MiotResponse
import miwu.miot.model.att.ActionList
import miwu.miot.model.att.PropertyList
import miwu.miot.service.body.ActionBody
import miwu.miot.service.body.GetParams
import miwu.miot.service.body.SetParams
import retrofit2.http.Body
import retrofit2.http.POST

interface MiotService {
    @POST("miotspec/prop/set")
    suspend fun setDeviceAtt(@Body body: SetParams): MiotResponse<PropertyList?>

    @POST("miotspec/prop/get")
    suspend fun getDeviceAtt(@Body body: GetParams): MiotResponse<PropertyList?>

    @POST("miotspec/action")
    suspend fun doAction(@Body body: ActionBody): MiotResponse<ActionList?>
}
