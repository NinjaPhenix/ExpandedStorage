pluginManagement {
    repositories {
        maven {
            name = "Architectury"
            url = uri("https://maven.architectury.dev/")
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
            content {
                excludeModule("org.eclipse.jdt", "org.eclipse.jdt.core")
                excludeModule("org.eclipse.platform", "org.eclipse.equinox.common")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://jitpack.io")
                    name = "JitPack"
                }
            }
            filter {
                includeGroup("com.gitlab.ninjaphenix")
                includeGroup("com.gitlab.ninjaphenix.gradle-utils")
            }
        }
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "expandedstorage"

//include("fabric")
include("forge")
