package miot.kotlin.helper

import miot.kotlin.Miot
import miot.kotlin.model.miot.MiotScenes

suspend fun MiotScenes.Result.Scene.runScene(miot: Miot) = miot.runScene(this)
