package miwu.miot

interface MiotBase64 {
    fun decode(str: String) : ByteArray

    fun encode(bytes: ByteArray) : String

    fun config(encode: (ByteArray) -> String, decode: (String) -> ByteArray)
}