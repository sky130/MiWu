package com.github.miwu.ui.device

import android.content.Context
import miwu.miot.kmp.utils.json
import miwu.miot.kmp.utils.to
import miwu.miot.model.att.SpecAtt
import miwu.support.api.Cache
import java.io.File

class AndroidCache(val context: Context) : Cache {

    override suspend fun getSpecAtt(urn: String): SpecAtt? =
        runCatching {
            att(urn)
                .takeIf(File::isFile)
                ?.readText()
                ?.to<SpecAtt>()
                ?.getOrThrow()
        }.getOrNull()

    override suspend fun putSpecAtt(urn: String, specAtt: SpecAtt) {
        runCatching {
            att(urn).writeText(json.encodeToString(specAtt))
        }
    }

    override suspend fun getLanguageMap(urn: String): Map<String, String>? =
        runCatching {
            map(urn)
                .takeIf(File::isFile)
                ?.readText()
                ?.to<Map<String, String>>()
                ?.getOrThrow()
        }.getOrNull()

    override suspend fun putLanguageMap(
        urn: String, map: Map<String, String>
    ) {
        runCatching {
            map(urn).writeText(json.encodeToString(map))
        }
    }

    private fun map(urn: String) = File("${context.cacheDir.absolutePath}/${urn.hashCode()}.map")

    private fun att(urn: String) = File("${context.cacheDir.absolutePath}/${urn.hashCode()}.att")

}