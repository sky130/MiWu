@file:Suppress("UNCHECKED_CAST")

package miot.kotlin.helper

import miot.kotlin.model.att.SpecAtt
import org.json.JSONObject

fun getLanguageMap(language: String, languageCode: String): Map<String, String>? {
    return try {
        JSONObject(language).getJSONObject("data").getJSONObject(languageCode).toMap() as Map<String, String>
    } catch (_: Exception) {
        null
    }
}

fun SpecAtt.convertLanguage(language: Map<String, String>): SpecAtt {
    for ((id, desc) in language) {
        val splitId = id.split(":")
        when (splitId.size / 2) {
            1 -> { // service
                val siid = splitId[1].toInt()
                run {
                    this.services.forEach {
                        if (it.iid == siid) {
                            it.description = desc
                            return@run
                        }
                    }
                }
            }

            2 -> { // property,action
                val siid = splitId[1].toInt()
                val piid = splitId[3].toInt()
                val type = splitId[2]
                this.services.forEach serviceForEach@{
                    if (it.iid == siid) {
                        when (type) {
                            "property" -> {
                                it.properties.forEach { property ->
                                    if (property.iid == piid) {
                                        property.description = desc
                                        return@serviceForEach
                                    }
                                }
                            }

                            "action" -> {
                                it.actions.forEach { action ->
                                    if (action.iid == piid) {
                                        action.description = desc
                                        return@serviceForEach
                                    }
                                }
                            }
                        }
                        return@serviceForEach
                    }
                }
            }

            3 -> { // valuelist
                val siid = splitId[1].toInt()
                val piid = splitId[3].toInt()
                val index = splitId[5].toInt()
                this.services.forEach serviceForEach@{
                    if (it.iid == siid) {
                        it.properties.forEach { property ->
                            if (property.iid == piid) {
                                property.valueList?.let { list ->
                                    list[index].description = desc
                                }
                                return@serviceForEach
                            }
                        }
                        return@serviceForEach
                    }
                }
            }

            else -> {}
        }
    }
    return this
}



