pluginManagement {
    repositories {
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.fabricmc.net/") }
        maven {
            url = uri("https://maven.minecraftforge.net/")
            content {
                excludeModule("org.eclipse.jdt", "org.eclipse.jdt.core")
                excludeModule("org.eclipse.platform", "org.eclipse.equinox.common")
            }
        }
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "expandedstorage"

include("fabric")
//include('forge')
