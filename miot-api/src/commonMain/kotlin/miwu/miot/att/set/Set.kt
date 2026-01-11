package miwu.miot.att.set

import miwu.miot.att.get.GetAtt

typealias SetAtt = Pair<GetAtt, Any>// siid,piid,value
val SetAtt.siid get() = this.first.first
val SetAtt.piid get() = this.first.second
val SetAtt.value get() = this.second

