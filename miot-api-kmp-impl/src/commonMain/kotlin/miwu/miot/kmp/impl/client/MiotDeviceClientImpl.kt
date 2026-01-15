package miwu.miot.kmp.impl.client

import miwu.miot.att.get.GetAtt
import miwu.miot.att.get.piid
import miwu.miot.att.get.siid
import miwu.miot.att.set.SetAtt
import miwu.miot.att.set.piid
import miwu.miot.att.set.siid
import miwu.miot.att.set.value
import miwu.miot.client.MiotDeviceClient
import miwu.miot.exception.MiotClientException
import miwu.miot.exception.MiotDeviceException
import miwu.miot.kmp.service.body.ActionBody
import miwu.miot.kmp.service.body.GetParams
import miwu.miot.kmp.service.body.SetParams
import miwu.miot.kmp.service.createMiotService
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice


class MiotDeviceClientImpl(private val user: MiotUser) : MiotDeviceClient {
    private val ktorfit = MiotAuthKtorfit(user)
    private val miotService = ktorfit.createMiotService()

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
            ActionBody.Action(device.did, siid, aiid)
                .apply { `in`.addAll(input) }
                .body()
        )
    }.recoverCatching {
        throw MiotClientException.actionFailed(device.did, siid, aiid, *input, it)
    }
}