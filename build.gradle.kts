
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.script.lang.kotlin.*
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class newTask(private val configuration: org.gradle.api.Task.() -> kotlin.Unit) : ReadOnlyProperty<KotlinBuildScript, Task> {
    private var task: Task? = null

    override fun getValue(thisRef: KotlinBuildScript, property: KProperty<*>): Task {
        return task ?: getTask(thisRef, property)
    }

    private fun getTask(thisRef: KotlinBuildScript, property: KProperty<*>): Task {
        val task = thisRef.task(property.name).doLast(configuration)
        this.task = task
        return task
    }
}

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
    plugin("com.jfrog.bintray")
}

dependencies {
    testCompile("junit:junit:4.12")
}

val sourceSets by project
val javadoc by project

val myproperties = Properties()
myproperties.load(project.rootProject.file("local.properties").inputStream())

(findProperty("bintray") as BintrayExtension).apply {
    user = myproperties["bintray.user"] as String
    key = myproperties["bintray.apikey"] as String
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
                passphrase = myproperties.getProperty("bintray.gpg.password")
            }
        }
    }
}

with(extra) {
    set("bintrayRepo", "maven")
    set("bintrayName", "retro-optional")
    set("publishedGroupId", "com.memoizr")
    set("libraryName", "retro-optional")
    set("artifact", "retro-optional")
    set("libraryDescription", "A backport of Java 8 optional for Java 7")
    set("siteUrl", "https://github.com/memoizr/retro-optional")
    set("gitUrl", "https://github.com/memoizr/retro-optional")
    set("libraryVersion", "0.9.3")
    set("developerId", "memoizr")
    set("developerName", "memoizr")
    set("developerEmail", "memoizrlabs@gmail.com")
    set("licenseName", "The Apache Software License, Version 2.0")
    set("licenseUrl", "http://www.apache.org/licenses/LICENSE-2.0.txt")
    set("allLicenses", listOf("Apache-2.0"))
}

setProperty("targetCompatibility", 1.7)
setProperty("sourceCompatibility", 1.7)

val sourcesJar = task<Jar>("sourcesJars") {
    dependsOn + "classes"
    classifier = "sources"
    from((sourceSets as DefaultSourceSetContainer).getByName("main").allSource)
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


inline fun Project.artifacts(configuration: KotlinArtifactsHandler.() -> Unit) =
        KotlinArtifactsHandler(artifacts).configuration()

infix fun Task.doLast(task: org.gradle.api.Task.() -> kotlin.Unit) {
    this.doLast(task)
}

fun defaultTasks(vararg property: KProperty<Task>) {
    defaultTasks(*property.map { it.name }.toTypedArray())
}

javaClass.declaredMethods.forEach {
    if (it.returnType == Task::class.java && it.name.startsWith("get")) {
        it.invoke(this)
    }
}

class KotlinArtifactsHandler(val artifacts: ArtifactHandler) : ArtifactHandler by artifacts {

    operator fun String.invoke(dependencyNotation: Any): PublishArtifact =
            artifacts.add(this, dependencyNotation)

    inline operator fun invoke(configuration: KotlinArtifactsHandler.() -> Unit) =
            configuration()
}
