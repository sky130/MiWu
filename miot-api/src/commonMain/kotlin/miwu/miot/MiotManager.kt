package miwu.miot

interface MiotManager {
    val Login : MiotLoginClient
    val SpecAtt : MiotSpecAttClient
    val Base64 : MiotBase64
}