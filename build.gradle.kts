plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.20"
}

group = "com.javaai"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.javaai"
            artifactId = "javaai-core"
            version = "1.0.0"

            pom {
                name.set("JavaAI Core")
                description.set("Simple AI agent for learning Minecraft player inputs")
                url.set("https://github.com/user/JavaAI")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "local"
            url = uri("C:\\Users\\danie\\.m2\\repository")
        }
    }
}
