import net.modgarden.silicate.gradle.Properties
import net.modgarden.silicate.gradle.Versions
import org.apache.tools.ant.filters.LineContains
import org.gradle.jvm.tasks.Jar

plugins {
	id("conventions.loader")
	id("net.neoforged.moddev")
	id("me.modmuss50.mod-publish-plugin")
}

dependencies {
	implementation("dev.yumi.commons:yumi-commons:${Versions.YUMI_COMMONS}") {
		jarJar(this)
		exclude("org.slf4j", "slf4j-api")
	}
}

neoForge {
	version = Versions.NEOFORGE
	parchment {
		minecraftVersion = Versions.PARCHMENT_MINECRAFT
		mappingsVersion = Versions.PARCHMENT
	}
	addModdingDependenciesTo(sourceSets["test"])

	val at = project(":common").file("src/main/resources/${Properties.MOD_ID}.cfg")
	if (at.exists())
		setAccessTransformers(at)
	validateAccessTransformers = true

	runs {
		configureEach {
			systemProperty("forge.logging.markers", "REGISTRIES")
			systemProperty("forge.logging.console.level", "debug")
			systemProperty("neoforge.enabledGameTestNamespaces", Properties.MOD_ID)
		}
		create("client") {
			client()
			gameDirectory.set(file("runs/client"))
			sourceSet = sourceSets["test"]
			jvmArguments.set(setOf("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true"))
		}
		create("server") {
			server()
			gameDirectory.set(file("runs/server"))
			programArgument("--nogui")
			sourceSet = sourceSets["test"]
			jvmArguments.set(setOf("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true"))
		}
		create("gameTestServer") {
			type = "gameTestServer"
			@Suppress("UnstableApiUsage") // respectfully, shut the f*#@ up
			gameDirectory = project.file("build/gametest")
		}
	}

	mods {
		register(Properties.MOD_ID) {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["test"])
		}
	}
}

tasks {
	named<ProcessResources>("processResources").configure {
		filesMatching("*.mixins.json") {
			filter<LineContains>("negate" to true, "contains" to setOf("refmap"))
		}
	}
}

publishMods {
	file.set(tasks.named<Jar>("jar").get().archiveFile)
	modLoaders.add("neoforge")
	changelog = rootProject.file("CHANGELOG.md").readText()
	displayName = "v${Versions.MOD} (NeoForge ${Versions.MINECRAFT})"
	version = "${Versions.MOD}+${Versions.MINECRAFT}-neoforge"
	type = BETA

	modrinth {
		projectId = Properties.MODRINTH_PROJECT_ID
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")

		minecraftVersions.add(Versions.MINECRAFT)
	}

	github {
		type = STABLE
		accessToken = providers.environmentVariable("GITHUB_TOKEN")
		parent(project(":common").tasks.named("publishGithub"))
	}
}
