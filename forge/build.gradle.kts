repositories {
    maven {
        // JEI maven
        name = "Progwml6 maven"
        url = uri("https://dvs1.progwml6.com/files/maven/")
    }
    maven {
        // JEI maven - fallback
        name = "ModMaven"
        url = uri("https://modmaven.k-4u.nl")
    }
}

dependencies {
    forge("net.minecraftforge:forge:${properties["minecraft_version"]}-${properties["forge_version"]}")

    // For base module
    modCompileOnly("mezz.jei:jei-${properties["minecraft_version"]}:${properties["jei_version"]}:api")
    modRuntime("mezz.jei:jei-${properties["minecraft_version"]}:${properties["jei_version"]}")
}

tasks.withType<ProcessResources>() {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") {
        expand(props)
    }
}
