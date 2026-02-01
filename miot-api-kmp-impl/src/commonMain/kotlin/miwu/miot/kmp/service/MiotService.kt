package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import miwu.miot.kmp.service.body.ActionBody
import miwu.miot.kmp.service.body.GetParams
import miwu.miot.kmp.service.body.SetParams
import miwu.miot.model.att.Action
import miwu.miot.model.att.DeviceAtt

interface MiotService {
    @POST("miotspec/prop/set")
    suspend fun setDeviceAtt(@Body body: SetParams): String

    @POST("miotspec/prop/get")
    suspend fun getDeviceAtt(@Body body: GetParams): DeviceAtt

    @POST("miotspec/action")
    suspend fun doAction(@Body body: ActionBody): Action
}