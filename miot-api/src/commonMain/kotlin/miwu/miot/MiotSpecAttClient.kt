package miwu.miot

import miwu.miot.model.att.SpecAtt

interface MiotSpecAttClient {

    /**
     * 获取某个 URN 的属性信息
     * @param urn 设备类型唯一标识
     * @return 属性信息
     */
    suspend fun getSpecAtt(urn: String): Result<SpecAtt>

    /**
     * 获取某个 URN 的多语言信息（JSON字符串）
     * @param urn 设备类型唯一标识
     * @return 多语言信息，JSON字符串
     */
    suspend fun getSpecMultiLanguage(urn: String): Result<String>

    /**
     * 获取带某种语言的属性信息
     * @param urn 设备类型唯一标识
     * @param languageCode 语言代码（如"zh_CN","en"等）
     * @return 属性信息（已转换为该语言），失败返回 null
     */
    suspend fun getSpecAttWithLanguage(urn: String, languageCode: String): Result<SpecAtt>

    /**
     * 获取语言映射表
     * @param language 多语言信息（JSON字符串）
     * @param languageCode 语言代码（如"zh_CN","en"等）
     * @return 语言映射表，失败返回 null
     */
    fun getSpecAttLanguageMap(language: String, languageCode: String): Result<Map<String, String>>

}
