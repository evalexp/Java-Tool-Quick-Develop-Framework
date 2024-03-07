import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "top.evalexp.tools"
version = "1.0-SNAPSHOT"
var sdk_version = "1.0.0"
var sdk_name = "JTQDF-SDK"
var release_version = "1.2.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-cli:commons-cli:1.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.formdev:flatlaf:3.2.5")
    implementation("com.miglayout:miglayout:+")
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
    version = release_version
    archiveFileName = "framework-${version}.jar"
}

tasks.create<Zip>("generate_sdk_src") {

    archiveBaseName = sdk_name
    archiveVersion = "$sdk_version-src"

    from("src/main/java/")


    include("top/evalexp/tools/interfaces/**")
    include("top/evalexp/tools/impl/component/**")
    include("top/evalexp/tools/impl/plugin/JTest*")
}

tasks.create<Jar>("generate_sdk_jar") {
    archiveBaseName = sdk_name
    archiveVersion = sdk_version

    from("build/classes/java/main")
    include("top/evalexp/tools/interfaces/**")
    include("top/evalexp/tools/impl/component/**")
    include("top/evalexp/tools/impl/plugin/JTest*")

    dependsOn(tasks.compileJava)
}

tasks.create("build_all") {
    dependsOn(tasks.getByName("generate_sdk_src"))
    dependsOn(tasks.getByName("generate_sdk_jar"))
    dependsOn(tasks.shadowJar)
}