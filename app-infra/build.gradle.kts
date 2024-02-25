dependencies {
    implementation(project(":app-core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.h2database:h2:2.1.214")
//    implementation("it.ozimov:embedded-redis:0.7.2")
    implementation("com.github.codemonstur:embedded-redis:1.0.0")
    implementation("org.redisson:redisson:3.18.0")

    runtimeOnly("com.h2database:h2")

    testImplementation(project(":app-core", "testArtifacts"))
    testImplementation(project(":app"))
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
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}
