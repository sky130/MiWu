package com.github.miwu.miot

import com.github.miwu.miot.device.DeviceType
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.utils.parseUrn

interface SpecAttHelper {

    fun DeviceType.forEachAtt(att: SpecAtt): DeviceType {
        for (service in att.services) {
            val serviceUrn = service.type.parseUrn()
            val name = serviceUrn.name
            val siid = service.iid
            service.properties?.let {
                for (property in it) {
                    val propertyUrn = property.type.parseUrn()
                    property.apply {
                        translate()
                        onPropertyFound(siid, name, iid, propertyUrn.name, service.description, this)
                    }
                }
            }
            service.actions?.let {
                for (action in it) {
                    val actionUrn = action.type.parseUrn()
                    action.apply {
                        action.translate()
                        onActionFound(siid, name, iid, actionUrn.name, service.description, this)
                    }
                }
            }
        }
        return this
    }

    fun onPropertyFound(
        siid: Int,
        service: String,
        piid: Int,
        property: String,
        serviceDesc: String,
        obj: SpecAtt.Service.Property,
    )

    fun onActionFound(
        siid: Int,
        service: String,
        aiid: Int,
        action: String,
        serviceDesc: String,
        obj: SpecAtt.Service.Action,
    )


}