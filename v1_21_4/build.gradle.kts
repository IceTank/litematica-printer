import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("maven-publish")
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

val archives_base_name: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project
//val malilib_version: String by project
//val litematica_projectid: String by project
//val litematica_fileid: String by project

val mod_version: String by project

dependencies {
//    implementation(project(":common"))
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.2.2")?.let { implementation(it)?.let { include(it) } }
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    //Replace Masa malilib with sakura-ryoko fork
    modImplementation("com.github.sakura-ryoko:malilib:1.21.4-0.23.2-sakura.6")
    //Replace masa litematica with sakura-ryoko fork
    modImplementation("com.github.sakura-ryoko:litematica:1.21.4-0.21.2-sakura.8")
    modImplementation("com.ptsmods:devlogin:3.5")
}

repositories {
    maven("https://masa.dy.fi/maven")
    maven("https://www.cursemaven.com")
    maven ("https://jitpack.io")
}

// Process resources
tasks.withType<ProcessResources> {
    inputs.property("version", mod_version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to mod_version))
    }
}

tasks.build {
    finalizedBy("renameJar")
}

tasks.create("renameJar") {
    val remapJar = tasks.getByName<RemapJarTask>("remapJar")
    val jarFile = remapJar.archiveFile.get().asFile

    doLast {
        val targetFile = File(jarFile.parent, "$archives_base_name-$mod_version-mc$minecraft_version.jar")
        println("Renaming ${jarFile.absolutePath} to ${targetFile.absolutePath}")
        jarFile.renameTo(targetFile)
    }
}

