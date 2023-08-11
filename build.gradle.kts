// build.gradle ：项目依赖配置文件。通过它可以配置项目第三方依赖、插件版本、插件版本更新记录等信息。
plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.8.21"
  id("org.jetbrains.intellij") version "1.15.0"
}

dependencies {
//  implementation("com.fasterxml.jackson.core:jackson-databind:2.12.7.1");
//  implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.9.6");
}

group = "com.optimus"
version = "1.1"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2022.2.5")
  type.set("IU") // Target IDE Platform
  plugins.set(listOf("com.intellij.java"))
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    // 版本更新记录
    sinceBuild.set("222")
    untilBuild.set("232.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
