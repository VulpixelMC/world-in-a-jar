import net.modgarden.silicate.gradle.Properties
import net.modgarden.silicate.gradle.Versions
import org.gradle.jvm.tasks.Jar

plugins {
	id("conventions.loader")
	id("fabric-loom")
	id("me.modmuss50.mod-publish-plugin")
}

repositories {
	maven {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${Versions.MINECRAFT}")
	mappings(loom.officialMojangMappings())

	modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC_API}")
	modLocalRuntime("com.terraformersmc:modmenu:${Versions.MOD_MENU}")
	implementation("dev.yumi.commons:yumi-commons:${Versions.YUMI_COMMONS}")?.let { include(it) }
}

loom {
	val aw = file("src/main/resources/${Properties.MOD_ID}.accesswidener")
	if (aw.exists())
		accessWidenerPath.set(aw)
	mixin {
		defaultRefmapName.set("${Properties.MOD_ID}.refmap.json")
	}
	mods {
		register(Properties.MOD_ID) {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["test"])
		}
	}
	runs {
		named("client") {
			client()
			configName = "Fabric Client"
			setSource(sourceSets["test"])
			ideConfigGenerated(true)
			vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
		}
		named("server") {
			server()
			configName = "Fabric Server"
			setSource(sourceSets["test"])
			ideConfigGenerated(true)
			vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
		}
		register("datagen") {
			server()
			configName = "Fabric Datagen"
			setSource(sourceSets["test"])
			ideConfigGenerated(true)
			vmArg("-Dfabric-api.datagen")
			vmArg("-Dfabric-api.datagen.output-dir=${file("../common/src/generated/resources")}")
			vmArg("-Dfabric-api.datagen.modid=${Properties.MOD_ID}")
			runDir("build/datagen")
		}
		register("gameTestServer") {
			inherit(runs.getByName("server"))
			configName = "Fabric GameTestServer"
			vmArgs("-Dfabric-api.gametest")
			vmArgs("-Dfabric-api.gametest.report-file=${layout.buildDirectory}/junit.xml")
			runDir("build/gametest")
		}
	}
}

tasks {
	named<ProcessResources>("processResources").configure {
		exclude("${Properties.MOD_ID}.cfg")
	}
}

publishMods {
	file.set(tasks.named<Jar>("remapJar").get().archiveFile)
	modLoaders.add("fabric")
	changelog = rootProject.file("CHANGELOG.md").readText()
	displayName = "v${Versions.MOD} (Fabric ${Versions.MINECRAFT})"
	version = "${Versions.MOD}+${Versions.MINECRAFT}-fabric"
	type = BETA

	modrinth {
		projectId = Properties.MODRINTH_PROJECT_ID
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")

		requires {
			slug = "fabric-api"

			version = Versions.FABRIC_API
		}

		minecraftVersions.add(Versions.MINECRAFT)
	}

	github {
		type = STABLE
		accessToken = providers.environmentVariable("GITHUB_TOKEN")
		parent(project(":common").tasks.named("publishGithub"))
	}
}
