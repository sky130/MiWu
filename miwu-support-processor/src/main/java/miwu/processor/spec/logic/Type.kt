package miwu.processor.spec.logic

data class Type(val types: List<String>) {
    fun toNameList() = types.map { Urn.parseFrom(it).name }
}

