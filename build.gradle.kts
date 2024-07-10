plugins {
    java
    `maven-publish`
}

allprojects {
    group = "com.vexsoftware"
    version = "3.0.0"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spongepowered.org/maven")
        maven("https://repo.velocitypowered.com/snapshots/")
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
        testImplementation("org.mockito:mockito-core:2.+")
    }

    tasks {
        processResources {
            expand(mapOf("app.version" to this.project.version))
        }

        test {
            useJUnitPlatform()
        }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
        repositories {
            maven {
                name = "MonumentaMaven"
                url = when (version.toString().endsWith("SNAPSHOT")) {
                    true -> uri("https://maven.playmonumenta.com/snapshots")
                    false -> uri("https://maven.playmonumenta.com/releases")
                }

                credentials {
                    username = System.getenv("USERNAME")
                    password = System.getenv("TOKEN")
                }
            }
        }
    }
}