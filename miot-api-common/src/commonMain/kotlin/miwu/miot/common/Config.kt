package miwu.miot.common

val RANDOM_TEMP_CHARS = "0123456789ABCDEF".toCharArray()
val LONG_TEMP_CHARS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

const val MI_HOME_USER_AGENT = "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS"
const val MIOT_SID = "mijia"
const val MIOT_SERVER_URL = "https://api.io.mi.com/app/"
const val SPEC_SERVER_URL = "https://miot-spec.org/"
const val SERVICE_LOGIN_URL = "https://account.xiaomi.com/pass/serviceLogin?sid=$MIOT_SID&_json=true"
const val SERVICE_LOGIN_AUTH_URL = "https://account.xiaomi.com/pass/serviceLoginAuth2"
const val QRCODE_GENERATE_URL = "https://account.xiaomi.com/longPolling/loginUrl"
const val MIOT_DEVICE_ICON_URL_PREFIX = "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model="