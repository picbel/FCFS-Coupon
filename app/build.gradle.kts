dependencies {
    implementation(project(":app-core"))
    implementation(project(":app-infra"))

    testImplementation(project(":app-core", "testArtifacts"))
    testImplementation(project(":app-infra", "testArtifacts"))
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