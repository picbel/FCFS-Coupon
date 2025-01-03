import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

allprojects {
    group = "com.fcfs"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-jpa")
    apply(plugin = "kotlin-allopen")

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.github.javafaker:javafaker:1.0.2") {
            exclude(group = "org.yaml", module = "snakeyaml")
        }
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
        testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }

    tasks.withType<Test> {
        outputs.upToDateWhen { false }
        useJUnitPlatform()
    }

    tasks.processTestResources {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<Test> {
    outputs.upToDateWhen { false }
    enabled = true
    useJUnitPlatform()
}


