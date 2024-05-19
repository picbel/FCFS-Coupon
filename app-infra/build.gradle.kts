dependencies {
    implementation(project(":app-core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.h2database:h2:2.1.214")
//    implementation("it.ozimov:embedded-redis:0.7.2")
    implementation("com.github.codemonstur:embedded-redis:1.0.0")
    implementation("org.redisson:redisson:3.18.0")

    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.4.0")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:3.4.0")
    implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.4.0")


    runtimeOnly("com.h2database:h2")

    testImplementation(project(":app-core", "testArtifacts"))
    testImplementation(project(":app"))
    testImplementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

sourceSets {
    val test by getting {
        java.srcDir("src/test/kotlin")
        resources.srcDir("src/test/resources")
        compileClasspath += project(":app-core").sourceSets["test"].runtimeClasspath
        runtimeClasspath += project(":app-core").sourceSets["test"].runtimeClasspath
    }
}

val testJar by tasks.registering(Jar::class) {
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

configurations {
    create("testArtifacts")
}

artifacts {
    add("testArtifacts", testJar)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}
