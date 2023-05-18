plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "net.bladehunt"
version = "0.1-beta"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://libraries.minecraft.net/")
    maven(uri("https://repo.extendedclip.com/content/repositories/placeholderapi/"))
    jcenter()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
    implementation("com.andreapivetta.kolor:kolor:1.0.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-minimessage:4.13.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.13.1")
    implementation("me.lucko:commodore:2.2")
    implementation("fr.mrmicky:fastboard:1.2.1")

    testImplementation(kotlin("test"))
}
tasks.build {
    dependsOn(tasks.shadowJar)
}
tasks.shadowJar {
    relocate("net.kyori.adventure","net.bladehunt.util.libs.adventure")
    relocate("net.kyori.examination","net.bladehunt.util.libs.examination")
    relocate("com.andreapivetta.kolor","net.bladehunt.util.libs.kolor")
    relocate("me.lucko.commodore","net.bladehunt.util.libs.commodore")
    relocate("fr.mrmicky.fastboard","net.bladehunt.util.libs.fastboard")
    dependencies {
        exclude(dependency("com.mojang:brigader"))
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}