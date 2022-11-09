plugins {
    signing
    `maven-publish`
    java
}

group = "xyz.novaserver"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.novaserver.xyz/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    compileOnly ("net.essentialsx:EssentialsX:2.19.7") {
        exclude(group = "org.bstats", module = "bstats-bukkit")
    }
    compileOnly("net.essentialsx:EssentialsXDiscord:2.19.7")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    compileOnly("xyz.novaserver.core:lib-paper:1.0-SNAPSHOT")
    compileOnly("xyz.novaserver.cutscenes:cutscenes-paper:2.0-SNAPSHOT")
    compileOnly("xyz.novaserver.placeholders:placeholders-paper:2.0-SNAPSHOT")
}

val targetJavaVersion = 17

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    jar {
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    build {
        dependsOn(jar)
    }
}

publishing {
    repositories {
        maven {
            name = "novaReleases"
            url = uri("https://repo.novaserver.xyz/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "novaSnapshots"
            url = uri("https://repo.novaserver.xyz/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String?
            artifactId = project.name
            version = project.version as String?
            from(components["java"])
        }
    }
}