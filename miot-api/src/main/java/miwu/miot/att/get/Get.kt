package miwu.miot.att.get

typealias GetAtt = Pair<Int, Int> // siid,piid

val GetAtt.siid get() = this.first
val GetAtt.piid get() = this.second