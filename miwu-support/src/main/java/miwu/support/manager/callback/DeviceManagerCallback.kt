package miwu.support.manager.callback

import miwu.miot.model.att.SpecAtt

interface DeviceManagerCallback {
    fun onDeviceInitiated()

    fun onDeviceAttLoaded(specAtt: SpecAtt) = Unit
}