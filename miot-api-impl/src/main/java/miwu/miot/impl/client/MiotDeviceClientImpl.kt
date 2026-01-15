package miwu.miot.impl.client

import miwu.miot.att.get.GetAtt
import miwu.miot.att.get.piid
import miwu.miot.att.get.siid
import miwu.miot.att.set.SetAtt
import miwu.miot.att.set.piid
import miwu.miot.att.set.siid
import miwu.miot.att.set.value
import miwu.miot.client.MiotDeviceClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.exception.MiotClientException
import miwu.miot.exception.MiotDeviceException
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.service.MiotService
import miwu.miot.service.body.ActionBody.Action
import miwu.miot.service.body.GetParams
import miwu.miot.service.body.SetParams
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create

class MiotDeviceClientImpl(private val user: MiotUser) : MiotDeviceClient {
    private val client = OkHttpClient {
        addInterceptor(MiotAuthInterceptor(user))
    }
    private val retrofit = Retrofit(
        baseUrl = MIOT_SERVER_URL,
        factories = arrayOf(
            JsonConverterFactory()
        ),
        client = client
    )
    private val miotService = retrofit.create<MiotService>()

    override suspend fun get(
        device: MiotDevice,
        att: Array<out GetAtt>
    ) = runCatching {
        val list = Array(att.size) {
            att[it].run { GetParams.Att(device.did, siid, piid) }
        }
        miotService.getDeviceAtt(GetParams(list))
    }.recoverCatching {
        val specType = device.specType ?: throw it
        throw MiotClientException.getSpecAttFailed(specType, it)
    }

    override suspend fun set(
        device: MiotDevice,
        att: Array<out SetAtt>
    ) = runCatching {
        val list = Array(att.size) {
            att[it].run { SetParams.Att(device.did, siid, piid, value) }
        }
        miotService.setDeviceAtt(SetParams(list))
        Unit
    }.recoverCatching {
        val specType = device.specType ?: throw MiotDeviceException.specNotFound(device.model)
        throw MiotClientException.getSpecAttFailed(specType, it)
    }

    override suspend fun action(
        device: MiotDevice,
        siid: Int,
        aiid: Int,
        vararg input: Any
    ) = runCatching {
        miotService.doAction(
            Action(device.did, siid, aiid)
                .apply { `in`.addAll(input) }
                .body()
        )
    }.recoverCatching {
        throw MiotClientException.actionFailed(device.did, siid, aiid, *input, it)
    }
}