loom {
    accessWidener = file("src/common/resources/expandedstorage.accessWidener")
}

repositories {
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io/")
        content {
            includeGroup("com.github.Virtuoel")
        }
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
}

tasks.withType<ProcessResources>() {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}
