package miwu.support.api

import miwu.miot.model.att.SpecAtt

interface Cache {

    suspend fun getSpecAtt(urn: String): SpecAtt?

    suspend fun putSpecAtt(urn: String, specAtt: SpecAtt)

    suspend fun getLanguageMap(urn: String): Map<String, String>?

    suspend fun putLanguageMap(urn: String, map: Map<String, String>)

}