import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    java
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.9"
    id ("com.github.johnrengelman.shadow") version "7.1.0"
    id ("org.beryx.runtime") version "1.12.7"
}


group = "com.rdude"
version = "1.0-SNAPSHOT"

javafx {
    modules("javafx.controls", "javafx.graphics", "javafx.web", "javafx.fxml")
}

application {
    mainClass.set("com.rdude.rpgeditork.FakeMain")
}


repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation(files("C:\\Java\\MyProjects\\RPG\\out\\artifacts\\RPG_jar\\RPG.jar"))
    implementation(files("C:\\Java\\MyProjects\\FxExLib\\jar\\FxExLib.jar"))
    implementation(group = "org.reflections", name = "reflections", version = "0.9.12")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.2")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.2")

    implementation("org.openjfx:javafx-controls:11.0.2")
    implementation("org.openjfx:javafx-graphics:11.0.2")
    implementation("org.openjfx:javafx-fxml:11.0.2")
    implementation("org.openjfx:javafx-web:11.0.2")

    implementation("org.ow2.asm:asm-util:9.1")

    implementation("org.openjdk.nashorn:nashorn-core:15.3")
}


tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    into("resources").from("resources")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}