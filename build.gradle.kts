import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { kotlin("jvm") version "1.6.20" }

group = "org.example"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  implementation("org.jfree:jfreesvg:3.4.2")
  implementation("org.apache.xmlgraphics:batik-transcoder:1.14")
  implementation("org.apache.xmlgraphics:batik-codec:1.14")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
  testImplementation("io.mockk:mockk:1.12.4")
  testImplementation("io.kotest:kotest-assertions-core:5.3.1")
}

tasks.test { useJUnitPlatform() }

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
