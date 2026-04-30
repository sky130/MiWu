package miwu.mock

import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotDeviceExtra

const val MOCK_PREFIX = "mock-"

/**
 * 构建 MockMiotDevice
 *
 * Mock 设备时, 填入默认的 specType 即可, 会自动加入 [MOCK_PREFIX]
 *
 * @param name 设备名称
 * @param did 设备 ID
 * @param model 设备型号
 * @param isOnline 是否在线
 * @param mac 设备 MAC 地址
 * @param uid 设备归属用户 ID
 */
@Suppress("FunctionName")
fun MockMiotDevice(
    name: String,
    did: String,
    model: String,
    specType: String,
    isOnline: Boolean = true,
    mac: String = "00:00:00:00:00:00",
    uid: String = "114514"
): MiotDevice {
    return MiotDevice(
        bssid = "",
        cnt = null,
        comFlag = 0,
        did = did,
        extra = MiotDeviceExtra(
            fwVersion = null,
            isSetPinCode = null,
            isSubGroup = null,
            mcuVersion = null,
            pinCodeType = null,
            platform = null,
            showGroupMember = null
        ),
        freqFlag = false,
        hideMode = 0,
        isOnline = isOnline,
        lastOnline = null,
        latitude = "",
        localIp = null,
        longitude = "",
        mac = mac,
        model = model,
        name = name,
        orderTime = 0,
        parentId = "",
        permitLevel = 0,
        pid = 0,
        rssi = 0,
        showMode = 0,
        specType = MOCK_PREFIX + specType,
        ssid = null,
        token = "",
        uid = uid.toLong()
    )
}