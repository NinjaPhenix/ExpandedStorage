import com.gitlab.ninjaphenix.gradle.task.MinifyJsonTask
import org.gradle.jvm.tasks.Jar

plugins {
    java
    id("dev.architectury.loom").version("0.8.0-SNAPSHOT").apply(false)
    id("com.gitlab.ninjaphenix.gradle-utils").version("0.0.20")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "dev.architectury.loom")

    group = properties["maven_group"] as String
    version = properties["mod_version"] as String
    base.archivesName.set(properties["archives_base_name"] as String)
    buildDir = rootDir.resolve("build/${project.name}")

    java {
        sourceCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
        targetCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
    }

    sourceSets {
        main {
            java {
                setSrcDirs(listOf(
                        "src/barrel/java",
                        "src/base/java",
                        "src/chest/java",
                        "src/old_chest/java",
                        "src/shulkerboxes/java",
                        rootDir.resolve("common/${project.name}Src/barrel/java"),
                        rootDir.resolve("common/${project.name}Src/base/java"),
                        rootDir.resolve("common/${project.name}Src/chest/java"),
                        rootDir.resolve("common/${project.name}Src/old_chest/java"),
                        rootDir.resolve("common/${project.name}Src/shulkerboxes/java")
                ))
            }
            resources {
                setSrcDirs(listOf(
                        "src/barrel/resources",
                        "src/base/resources",
                        "src/chest/resources",
                        "src/old_chest/resources",
                        "src/shulkerboxes/resources",
                        "src/common/resources",
                        rootDir.resolve("common/${project.name}Src/barrel/resources"),
                        rootDir.resolve("common/${project.name}Src/base/resources"),
                        rootDir.resolve("common/${project.name}Src/chest/resources"),
                        rootDir.resolve("common/${project.name}Src/old_chest/resources"),
                        rootDir.resolve("common/${project.name}Src/shulkerboxes/resources"),
                        rootDir.resolve("common/${project.name}Src/common/resources"),
                        rootDir.resolve("common/${project.name}Src/chest_compat/resources")
                ))
            }
        }
    }

    dependencies {
        annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.1")
        compileOnly("com.github.bsideup.jabel:jabel-javac-plugin:0.4.1")
    }

    val minecraft_java_version : String by project
    val isNotIdeaSync = System.getProperties().containsKey("idea.sync.active").not()

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (isNotIdeaSync) {
            options.release.set(minecraft_java_version.toInt())
        }
    }

    tasks.withType<JavaExec>().configureEach {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(minecraft_java_version.toInt()))
        })
    }

    val remapJarTask : Jar = tasks.getByName<Jar>("remapJar") {
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-fat.jar")
    }

    tasks.getByName<Jar>("jar") {
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-dev.jar")
    }

    val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
        parent.set(remapJarTask.outputs.files.singleFile)
        filePatterns.set(listOf("**/*.json", "**/*.mcmeta"))
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}.jar")
        dependsOn(remapJarTask)
    }

    tasks.getByName("build") {
        dependsOn(minifyJarTask)
    }
}

tasks.register("buildMod") {
    subprojects.forEach {
        dependsOn(it.tasks["build"])
    }
}
