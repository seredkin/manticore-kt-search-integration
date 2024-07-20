plugins {
  kotlin("jvm") version "2.0.0"
  kotlin("plugin.serialization") version "2.0.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven {
    name = "kt search repository"
    url = uri("https://maven.tryformation.com/releases")
  }
}

val http4kVersion = "4.9.9.0"
val ktorClientVersion = "1.6.3"
val tcVersion = "1.20.0"
dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.http4k:http4k-core:$http4kVersion")
  implementation("org.http4k:http4k-client-apache:$http4kVersion")
  implementation("org.http4k:http4k-format-gson:$http4kVersion")
  implementation("com.jillesvangurp:search-client-jvm:2.1.25")
  implementation("com.jillesvangurp:search-dsls-jvm:2.1.25")
  implementation("com.jillesvangurp:json-dsl-jvm:3.0.0")
  
  testImplementation("io.kotest:kotest-assertions-core:5.9.1")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("com.google.code.gson:gson:2.11.0")
  testImplementation("org.testcontainers:testcontainers:$tcVersion")
  testImplementation("org.testcontainers:junit-jupiter:$tcVersion")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
