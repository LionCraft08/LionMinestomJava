plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.lionk"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.minestom:minestom:2025.10.31-1.21.10")
    // https://mvnrepository.com/artifact/net.kyori/adventure-text-minimessage
    implementation("net.kyori:adventure-text-minimessage:4.25.0")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "dev.lionk.Main" // Change this to your main class
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}

tasks.test {
    useJUnitPlatform()
}