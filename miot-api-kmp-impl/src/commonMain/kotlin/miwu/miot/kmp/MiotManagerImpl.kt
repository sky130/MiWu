package miwu.miot.kmp

import miwu.miot.MiotManager

class MiotManagerImpl : MiotManager {
    override val Login = MiotLoginClientImpl()
    override val SpecAtt = MiotSpecAttClientImpl()
    override val Base64 get() = MiotBase64Impl
}



