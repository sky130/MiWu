package miwu.miot.att.action

import miwu.miot.att.get.GetAtt

typealias Action = Pair<GetAtt, List<Any>> //siid,piid,in
val Action.siid get() = this.first.first
val Action.piid get() = this.first.second
val Action.input get() = this.second