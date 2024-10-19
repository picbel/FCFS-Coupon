dependencies {
    implementation(project(":app-core"))
    implementation(project(":app-infra"))
    implementation(project(":app")) // TODO 삭제후 admin api 공통 로직은 모듈추출해서 써야함

    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

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