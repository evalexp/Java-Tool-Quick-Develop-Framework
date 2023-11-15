import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "top.evalexp.tools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-cli:commons-cli:+")
    implementation("com.fasterxml.jackson.core:jackson-databind:+")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "top.evalexp.tools.Main"))
    }
}

tasks.shadowJar {
    exclude("META-INF/**")
}

tasks.create<Zip>("generate_sdk") {

    archiveBaseName = "XuanYuanSDK"
    version = "1.0.0"

    from("src/main/java/")

    include("top/evalexp/tools/interfaces/**")
    include("top/evalexp/tools/impl/component/**")
    include("top/evalexp/tools/impl/plugin/JTest*")
}