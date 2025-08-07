package miwu.miot

import java.util.Base64

@Suppress("NewApi")
object MiotBase64Impl : MiotBase64 {
    internal var base64Encode: (ByteArray) -> String = { Base64.getEncoder().encodeToString(it) }
    internal var base64Decode: (String) -> ByteArray = { Base64.getDecoder().decode(it) }

    override fun decode(str: String) = base64Decode(str)

    override fun encode(bytes: ByteArray) = base64Encode(bytes)

    override fun config(encode: (ByteArray) -> String, decode: (String) -> ByteArray) {
        base64Encode = encode
        base64Decode = decode
    }
}