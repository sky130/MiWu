package miwu

import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

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

class MiwuPublishingPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<MiwuPublishingExtension>("miwuPublishing")
        project.pluginManager.apply("com.vanniktech.maven.publish")
        configurePublishing(project, extension)
    }

    private fun configurePublishing(project: Project, ext: MiwuPublishingExtension) {
        project.extensions.configure<MavenPublishBaseExtension> {
            project.plugins.withId("com.android.library") {
                configure(
                    AndroidSingleVariantLibrary(
                        javadocJar = JavadocJar.Javadoc(),
                        sourcesJar = SourcesJar.Sources(),
                        variant = "release",
                    )
                )
            }
            publishToMavenCentral()
            signAllPublications()
            coordinates(ext.group, ext.artifactId ?: project.name, ext.version)
            pom {
                name.set(ext.name)
                description.set(ext.description)
                inceptionYear.set(ext.inceptionYear)
                url.set(ext.url)
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/sky130/MiWu/refs/heads/master/LICENSE")
                        distribution.set("https://raw.githubusercontent.com/sky130/MiWu/refs/heads/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("sky130")
                        name.set("Sky233")
                        url.set("https://github.com/sky130/")
                        email.set("sky233ml@qq.com")
                        organization.set("")
                        organizationUrl.set("")
                    }
                }
                scm {
                    url.set("https://github.com/sky130/MiWu")
                    connection.set("scm:git:git://github.com/sky130/MiWu.git")
                    developerConnection.set("scm:git:ssh://git@github.com/sky130/MiWu.git")
                }
            }
        }
    }

}
