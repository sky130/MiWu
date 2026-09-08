package miwu

open class MiwuPublishingExtension {
    var name: String? = null
    var group: String? = null
    var artifactId: String? = null
    var url: String? = "https://github.com/sky130/MiWu"
    var version: String? = "3.0.0"
    var description: String? = null
    var inceptionYear: String? = null

    fun autoVersion() = latestGitTag
}