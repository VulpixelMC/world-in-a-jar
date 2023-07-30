@file:Suppress("UnstableApiUsage")

import nl.javadude.gradle.plugins.license.License

plugins {
	id("com.github.hierynomus.license").version("0.16.1")
	id("org.jetbrains.kotlin.jvm").version(libs.versions.kotlin)
	alias(libs.plugins.quilt.loom)
	`maven-publish`
}

val modVersion: String by project
val mavenGroup: String by project
val modId: String by project

base.archivesBaseName = modId
version = modVersion
group = mavenGroup

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
	maven {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/")
	}
	
	maven {
		name = "Modrinth"
		url = uri("https://api.modrinth.com/maven")
		content {
			includeGroup("maven.modrinth")
		}
	}
	
	maven {
		url = uri("https://maven.bai.lol")
	}
	
	maven {
		name = "Cursed Maven"
		url = uri("https://cursemaven.com")
		content {
			includeGroup("curse.maven")
		}
	}
	
	maven {
		url = uri("https://repo.minelittlepony-mod.com/maven/release")
	}
	
	maven {
		name = "Gegy"
		url = uri("https://maven.gegy.dev")
	}
	
	maven {
		url = uri("https://nexus.velocitypowered.com/repository/maven-public/")
	}
	
	maven {
		name = "QuiltMC Snapshot"
		url = uri("https://maven.quiltmc.org/repository/snapshot")
	}
}

val modImplementationInclude by configurations.register("modImplementationInclude")

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
	minecraft(libs.minecraft)
	mappings(loom.layered {
		mappings("org.quiltmc:quilt-mappings:${libs.versions.quilt.mappings.get()}:intermediary-v2")
	})
	modImplementation(libs.quilt.loader)
	modImplementation(libs.quilt.lang.kotlin) {
		exclude(group = "org.quiltmc.qsl.item")
		exclude(group = "org.quiltmc.qsl.entity")
	}
	
	modImplementation(libs.core.qsl.base)
	modImplementation(libs.core.networking)
	
	modImplementation(libs.block.entity)
	modImplementation(libs.block.extensions)
	
	modImplementation(libs.item.content.registry)
	modImplementation(libs.item.setting)
	
//	implementation(include("net.auoeke", "reflect", "5.+"))
//	implementation(include("net.gudenau.lib", "unsafe", "latest.release"))
//	implementation(include("org.objenesis", "objenesis", "3.3"))
	
//	implementation("net.bytebuddy", "byte-buddy-agent", "1.12.+")
//	modImplementation("maven.modrinth", "yqh", "0.1.2")
//	modImplementation("maven.modrinth", "sodium", "mc1.19.4-0.4.10")
	
	// QSL is not a complete API; You will need Quilted Fabric API to fill in the gaps.
	// Quilted Fabric API will automatically pull in the correct QSL version.
	modImplementation(libs.quilted.fabric.api)
	// modImplementation libs.bundles.quilted.fabric.api // If you wish to use Fabric API's deprecated modules, you can replace the above line with this one
	
	modCompileOnly("mcp.mobius.waila:wthit-api:quilt-8.1.1")
	
	modRuntimeOnly("com.terraformersmc", "modmenu", "7.1.0")
}

tasks.processResources {
	inputs.property("version", version)
	
	filesMatching("quilt.mod.json") {
		expand("group" to mavenGroup, "id" to modId, "version" to version)
	}
	
	filesMatching("**/lang/*.json") {
		expand("id" to modId)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	// Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
	options.release.set(17)
}

loom {
	accessWidenerPath.set(file("src/main/resources/$modId.accesswidener"))
}

java {
	// Still required by IDEs such as Eclipse and Visual Studio Code
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
	
	// If this mod is going to be a library, then it should also generate Javadocs in order to aid with developement.
	// Uncomment this line to generate them.
	// withJavadocJar()
}

// If you plan to use a different file for the license, don't forget to change the file name here!
tasks.withType<AbstractArchiveTask> {
	from("COPYING") {
		rename { "${it}_${modId}" }
	}
	
	from("COPYING.LESSER") {
		rename { "${it}_${modId}" }
	}
}

tasks.build {
	dependsOn(tasks.license)
}

tasks.withType<License> {
	header = file("LHEADER")
	exclude("**/*.json")
}

// Configure the maven publication
publishing {
	publications { }
	
	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
