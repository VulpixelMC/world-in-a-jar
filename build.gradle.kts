@file:Suppress("UnstableApiUsage")

import nl.javadude.gradle.plugins.license.License

plugins {
	id("com.github.hierynomus.license").version("0.16.1")
	alias(libs.plugins.fabric.loom)
	`maven-publish`
}

val modVersion: String by project
val mavenGroup: String by project
val modId: String by project

base.archivesName = modId
version = modVersion
group = mavenGroup

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
	
	mavenCentral()
	
	maven {
		name = "ParchmentMC"
		url = uri("https://maven.parchmentmc.org")
	}
	
	// Mod Artifacts
	
	maven {
		name = "WTHIT Maven"
		url = uri("https://maven2.bai.lol")
		content {
			includeGroup("lol.bai")
			includeGroup("mcp.mobius.waila")
		}
	}
	
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
		name = "Gegy Maven"
		url = uri("https://maven.gegy.dev/releases")
		content {
			includeGroup("dev.gegy")
		}
	}
	
	maven {
		name = "Nucleoid Maven"
		url = uri("https://maven.nucleoid.xyz/")
		content {
			includeGroup("xyz.nucleoid")
		}
	}
}

val modImplementationInclude by configurations.register("modImplementationInclude")

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
	minecraft(libs.minecraft)
	mappings(loom.layered {
		officialMojangMappings()
		parchment(libs.parchment)
	})
	
	// Loader
	modImplementation(libs.fabric.loader)
	
	// Libraries
	modImplementation(libs.fabric.api)
	modImplementation(libs.playerroles.api) {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc")
	}
	include(libs.playerroles.api)
	
	// Mod Integrations
	modCompileOnly(libs.wthit)
	modCompileOnly(libs.wthit.api)
	modImplementation(libs.sodium)
	modImplementation(libs.bedrockbreaking)
	modRuntimeOnly(libs.wthit)
	modRuntimeOnly(libs.modmenu) {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc")
	}
	modRuntimeOnly(libs.playerroles) {
		exclude(group = "net.fabricmc.fabric-api")
		exclude(group = "net.fabricmc")
	}
	modRuntimeOnly(libs.resource.explorer)
}

tasks.processResources {
	inputs.property("version", version)
	
	filesMatching("fabric.mod.json") {
		expand("group" to mavenGroup, "id" to modId, "version" to version)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	// Minecraft 1.21 upwards uses Java 21.
	options.release.set(21)
}

loom {
	accessWidenerPath.set(file("src/main/resources/$modId.accesswidener"))
}

fabricApi {
	configureDataGeneration()
}

java {
	// Still required by IDEs such as Eclipse and Visual Studio Code
	sourceCompatibility = JavaVersion.VERSION_22
	targetCompatibility = JavaVersion.VERSION_21
	
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

tasks.license.configure {
	mustRunAfter(tasks.licenseFormat)
}

tasks.build {
	dependsOn(tasks.licenseFormat)
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
