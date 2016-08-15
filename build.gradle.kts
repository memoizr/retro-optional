
import build.jUnit
import com.jfrog.bintray.gradle.BintrayExtension
import org.apache.xml.serialize.OutputFormat
import org.apache.xml.serialize.XMLSerializer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.maven.MavenPom
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.MavenPluginConvention
import org.gradle.api.tasks.javadoc.Javadoc
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

configure<JavaPluginConvention> {
    val targetJavaVersion = 1.7

    setTargetCompatibility(targetJavaVersion)
    setSourceCompatibility(targetJavaVersion)
}

dependencies {
    testCompile(jUnit)
    testCompile("nl.jqno.equalsverifier:equalsverifier:2.1.5")
}

val sourcesJar = task<Jar>("sourcesJars") {
    dependsOn + "classes"
    classifier = "sources"
    from(the<JavaPluginConvention>().sourceSets.getByName("main").allSource)
}

val javadocJar = task<Jar>("javadocJar") {
    dependsOn + "javadoc"
    classifier = "javadoc"
    from("javadoc"<Javadoc>().destinationDir)
}

operator inline fun <reified T: Any> String.invoke() = getTask<T>(this)

artifacts {
    artifacts.add("archives", sourcesJar)
    artifacts.add("archives", javadocJar)
}

"jacocoTestReport"<JacocoReport> {
    reports {
        it.xml.isEnabled = true
    }
}

configure<BintrayExtension> {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_APIKEY")
    setConfigurations("archives")

    pkg configure {
        repo = "maven"
        name = "retro-optional"
        desc = "A backport of Java 8 optional for Java 7"
        websiteUrl = "https://github.com/memoizr/retro-optional"
        vcsUrl = "https://github.com/memoizr/retro-optional"
        publish = true
        publicDownloadNumbers = false

        version configure {
            desc = "A backport of Java 8 optional for Java 7"
            gpg configure {
                sign = true
                passphrase = System.getenv("BINTRAY_CREDENTIAL")
            }
        }
    }
}

task("createPom").doLast {
    configure<MavenPluginConvention> {
        pom {
            project configure {
                groupId = "com.memoizr"
                artifactId = "retro-optional"

                val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

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

fun MavenPluginConvention.pom(configuration: MavenPom.() -> Unit) = pom().apply(configuration)

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

operator inline fun <reified T: Any> String.invoke(conf: T.() -> Unit) = getTask<T>(this).apply(conf)

infix fun <T> T.configure(configuration: T.() -> Unit) = apply(configuration)

inline fun <reified T : Any> getTask(name: String) = tasks.getByName(name) as T

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

class BooPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("tadaaa")
    }
}
