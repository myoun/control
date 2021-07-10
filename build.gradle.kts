plugins {
    kotlin("jvm") version "1.5.0"
}

group = "kr.myoung2"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    implementation("io.github.monun:kommand:1.2.1")
}

val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())

tasks {

    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }

    jar {
        from (shade.map { if (it.isDirectory) it else zipTree(it) })
    }
}