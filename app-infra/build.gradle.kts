dependencies {
    implementation(project(":app-core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.h2database:h2:2.1.214")
    implementation("it.ozimov:embedded-redis:0.7.2")
    implementation("org.redisson:redisson:3.18.0")

    runtimeOnly("com.h2database:h2")

    testImplementation(project(":app-core", "testArtifacts"))
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
