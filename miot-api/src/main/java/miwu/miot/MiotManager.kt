package miwu.miot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.model.miot.MiotDevices.Result.Device.Info

interface MiotManager {
    val Login : MiotLoginClient
    val SpecAtt : MiotSpecAttClient
    val Base64 : MiotBase64


}