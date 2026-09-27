package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import miwu.miot.model.request.ActionBody
import miwu.miot.model.request.GetParams
import miwu.miot.model.request.SetParams
import miwu.miot.model.MiotResponse
import miwu.miot.model.att.ActionList
import miwu.miot.model.att.PropertyList

interface MiotService {
    @POST("miotspec/prop/set")
    suspend fun setDeviceAtt(@Body body: SetParams): MiotResponse<PropertyList>

    @POST("miotspec/prop/get")
    suspend fun getDeviceAtt(@Body body: GetParams): MiotResponse<PropertyList>

    @POST("miotspec/action")
    suspend fun doAction(@Body body: ActionBody): MiotResponse<ActionList>
}
