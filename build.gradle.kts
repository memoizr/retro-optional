import com.jfrog.bintray.gradle.BintrayExtension
import org.apache.xml.serialize.OutputFormat
import org.apache.xml.serialize.XMLSerializer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.plugins.MavenPluginConvention
import org.gradle.jvm.tasks.Jar
import org.gradle.script.lang.kotlin.*
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory

version = "v0.1.4"

buildscript {
    repositories {
        jcenter()
        gradleScriptKotlin()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7")
    }
}

repositories {
    jcenter()
    gradleScriptKotlin()
}

apply {
    plugin("java")
    plugin("maven")
    plugin("jacoco")
    plugin("com.jfrog.bintray")
}


setProperty("targetCompatibility", 1.7)
setProperty("sourceCompatibility", 1.7)

dependencies {
    testCompile("junit:junit:4.12")
    testCompile("nl.jqno.equalsverifier:equalsverifier:2.1.5")
}

val javadoc by project
val sourceSets = the<JavaPluginConvention>().sourceSets

val sourcesJar = task<Jar>("sourcesJars") {
    dependsOn + "classes"
    classifier = "sources"
    from(sourceSets.getByName("main").allSource)
}

val javadocJar = task<Jar>("javadocJar") {
    dependsOn + "javadoc"
    classifier = "javadoc"
    from(getTasksByName("javadoc", false).first().property("destinationDir"))
}

artifacts {
    artifacts.add("archives", sourcesJar)
    artifacts.add("archives", javadocJar)
}

(getTasksByName("jacocoTestReport", false).first() as JacocoReport).apply {
    reports {
        it.xml.isEnabled = true
    }
}

(findProperty("bintray") as BintrayExtension).apply {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_APIKEY")
    setConfigurations("archives")
    pkg.apply {
        repo = "maven"
        name = "retro-optional"
        desc = "A backport of Java 8 optional for Java 7"
        websiteUrl = "https://github.com/memoizr/retro-optional"
        vcsUrl = "https://github.com/memoizr/retro-optional"
        publish = true
        publicDownloadNumbers = false
        version.apply {
            desc = "A backport of Java 8 optional for Java 7"
            gpg.apply {
                sign = true
                passphrase = System.getenv("BINTRAY_CREDENTIAL")
            }
        }
    }
}

val mavenPluginConvention = convention.findPlugin(MavenPluginConvention::class.java)

val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
task("createPom") {
    doLast {
        mavenPluginConvention.pom().apply {
            project.apply {
                groupId = "com.memoizr"
                artifactId = "retro-optional"
                withXml {
                    val xml = it.asString()
                    val document: Document = documentBuilder.parse(xml.toString().byteInputStream())

                    document.append {
                        "licenses" {
                            "license" {
                                "name"("The Apache Software License, Version 2.0")
                                "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                "distribution"("repo")
                            }
                        }
                    }

                    val format = OutputFormat(document)
                    format.indenting = true
                    val writer = StringWriter()
                    val serializer = XMLSerializer(writer, format)
                    serializer.serialize(document)

                    xml.setLength(0)
                    xml.append(writer)
                }
            }
        }.writeTo("build/poms/pom-default.xml")
    }
}

class DocuBuilder(private val document: Document, private val parent: Node) {
    operator fun String.invoke(content: DocuBuilder.() -> Unit) {
        val element = document.createElement(this)
        DocuBuilder(document, element).content()
        parent.appendChild(element)
    }

    operator fun String.invoke(text: String) {
        val element = document.createElement(this)
        element.appendChild(document.createTextNode(text))
        parent.appendChild(element)
    }
}

fun Document.append(content: DocuBuilder.() -> Unit) {
    DocuBuilder(this, firstChild).content()
}

inline fun Project.artifacts(configuration: KotlinArtifactsHandler.() -> Unit) =
        KotlinArtifactsHandler(artifacts).configuration()

class KotlinArtifactsHandler(val artifacts: ArtifactHandler) : ArtifactHandler by artifacts {

    operator fun String.invoke(dependencyNotation: Any): PublishArtifact =
            artifacts.add(this, dependencyNotation)

    inline operator fun invoke(configuration: KotlinArtifactsHandler.() -> Unit) =
            configuration()
}

infix fun Task.doLast(task: org.gradle.api.Task.() -> kotlin.Unit) {
    this.doLast(task)
}
