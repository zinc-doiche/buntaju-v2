import org.hidetake.groovy.ssh.connection.AllowAnyHosts
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler

plugins {
    val kotlinVersion = "2.0.10"

    kotlin("jvm") version kotlinVersion
    id("org.hidetake.ssh") version "2.11.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "zinc.doiche"
version = "1.0"

val server = Remote(
    mapOf<String, Any>(
        "host" to project.property("host") as String,
        "port" to (project.property("port") as String).toInt(),
        "user" to project.property("user") as String,
        "password" to project.property("password") as String,
        "knownHosts" to AllowAnyHosts.instance
    )
)

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.4")
    implementation("org.mongodb:bson-kotlinx:5.1.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("club.minnced:jda-ktx:0.12.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.11.0")
}

configurations.implementation.configure {
    isCanBeResolved = true
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "zinc.doiche.MainKt"
        }
    }
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    javadoc {
        options.encoding = "UTF-8"
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.create(name = "deploy") {
    dependsOn("shadowJar")

    doLast {
        ssh.run(delegateClosureOf<RunHandler> {
            session(server, delegateClosureOf<SessionHandler> {
                val file = "$projectDir/build/libs/${project.name}-${project.version}-all.jar"
                val directory = "/home/buntaju/server"

                put(
                    hashMapOf(
                        "from" to file,
                        "into" to directory
                    )
                )
            })
        })
    }
}