package com.github.miwu.miot

import miot.kotlin.model.att.SpecAtt
import miot.kotlin.utils.parseUrn

interface SpecAttHelper {

    fun initAtt(att: SpecAtt) {
        for (service in att.services) {
            val serviceUrn = service.type.parseUrn()
            val value = serviceUrn.value
            val siid = service.iid
            for (property in service.properties) {
                val propertyUrn = property.type.parseUrn()
                property.apply {
                    onPropertyFound(siid, value, iid, propertyUrn.value, this)
                }
            }
            for (action in service.actions) {
                val actionUrn = action.type.parseUrn()
                action.apply {
                    onActionFound(siid, value, iid, actionUrn.value, this)
                }
            }
        }
    }

    fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        obj: SpecAtt.Service.Property,
    )

    fun onActionFound(
        siid: Int,
        service: String,
        piid: Int,
        action: String,
        obj: SpecAtt.Service.Action,
    )


}