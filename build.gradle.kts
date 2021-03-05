import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    java
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.9"
}


group = "com.rdude"
version = "1.0-SNAPSHOT"

javafx {
    modules("javafx.controls", "javafx.graphics")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation(files("C:\\Java\\MyProjects\\rpgEditorK\\RPG-E_jar.jar"))
    implementation(files("C:\\Java\\MyProjects\\FxLib\\FxExLib\\jar\\FxExLib.jar"))
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}