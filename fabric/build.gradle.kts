plugins {
    id("dev.architectury.loom")
}

loom {
    silentMojangMappingsLicense()
    useFabricMixin = true

    accessWidener = file("src/common/resources/expandedstorage.accessWidener")

    customManifest = "https://gist.githubusercontent.com/modmuss50/6e00baf80dcbaa42f3a2fc846b290128/raw/f035ef8f75164f56ec6352809a34841d326bae1c/1_18_experimental-snapshot-1.json"
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings(loom.officialMojangMappings())
}

repositories {
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "TerraformersMC"
                url = uri("https://maven.terraformersmc.com/")
            }
        }
        filter {
            includeGroup("com.terraformersmc")
        }
    }
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io/")
        content {
            includeGroup("com.github.Virtuoel")
        }
    }
    maven {
        name = "Siphalor's Maven"
        url = uri("https://maven.siphalor.de/")
    }
}

val excludeFabric: (ExternalModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${properties["fabric_loader_version"]}")
    modApi("net.fabricmc.fabric-api:fabric-api:${properties["fabric_api_version"]}")

    // For chest module
    modCompileOnly("com.github.Virtuoel:Statement:31a2c3f", excludeFabric)
    modCompileOnly("com.github.Virtuoel:Towelette:e5e39eb", excludeFabric)

    // For base module
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${properties["rei_version"]}", excludeFabric)
    modRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${properties["rei_version"]}")

    modCompileOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}", excludeFabric)
    modRuntime("com.terraformersmc:modmenu:${properties["modmenu_version"]}")

    modCompileOnly("de.siphalor:amecsapi-1.17:1.1+")
}

tasks.withType<ProcessResources>() {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}
