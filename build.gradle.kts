import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import java.io.File

plugins {
    java
    `java-library`
    id("net.neoforged.moddev") version "2.0.141" apply false
}

fun requiredString(name: String): String =
    findProperty(name)?.toString() ?: throw GradleException("Missing Gradle property: $name")

fun optionalInt(name: String): Int? =
    findProperty(name)?.toString()?.toInt()

fun File.collectPngResourceNames(): List<String> {
    if (!exists()) {
        return emptyList()
    }
    return walkTopDown()
        .filter { it.isFile && it.extension == "png" }
        .map { it.relativeTo(this).invariantSeparatorsPath.removeSuffix(".png") }
        .distinct()
        .sorted()
        .toList()
}

fun collectPngResourceNames(resourceRoots: List<File>, relativePath: String): List<String> =
    resourceRoots.asSequence()
        .map { File(it, relativePath) }
        .filter { it.exists() }
        .flatMap { it.collectPngResourceNames().asSequence() }
        .distinct()
        .sorted()
        .toList()

fun buildGeneratedComponentAtlasRegistrationSource(
    packageName: String,
    sprites: List<String>
): String = buildString {
    appendLine("package $packageName;")
    appendLine()
    appendLine("public final class GeneratedComponentAtlasRegistration {")
    appendLine("    private GeneratedComponentAtlasRegistration() {")
    appendLine("    }")
    appendLine()
    appendLine("    public static void register(RegisterComponentSpritesEvent event) {")
    for (sprite in sprites) {
        appendLine("        event.register(\"$sprite\");")
    }
    appendLine("    }")
    appendLine("}")
}

fun writeIfChanged(target: File, content: String) {
    target.parentFile.mkdirs()
    if (!target.exists() || target.readText() != content) {
        target.writeText(content)
    }
}

group = requiredString("root_package")
version = requiredString("mod_version")

val isVersionProject = project != rootProject && findProperty("minecraft_version") != null

if (isVersionProject) {
    val modId = requiredString("mod_id")
    val modName = requiredString("mod_name")
    val modVersion = requiredString("mod_version")
    val modAuthors = requiredString("mod_authors")
    val modDescription = requiredString("mod_description")
    val minecraftVersion = requiredString("minecraft_version")
    val minecraftVersionRange = requiredString("minecraft_version_range")
    val loaderVersionRange = requiredString("loader_version_range")
    val neoVersionRange = requiredString("neo_version_range")
    val resourcePackFormat = requiredString("resource_pack_format")
    val configuredJavaVersion = optionalInt("java_version") ?: 25
    val configuredRunJavaVersion = optionalInt("run_java_version") ?: configuredJavaVersion
    val mixinConfigFile = "mixins.$modId.json"

    val sharedJavaDir = rootProject.file("src/main/java")
    val sharedResourcesDir = rootProject.file("src/main/resources")
    val localJavaDir = file("src/main/java")
    val localResourcesDir = file("src/main/resources")
    val javaRoots = listOf(sharedJavaDir, localJavaDir)
        .distinctBy { it.absolutePath }
        .filter { it.exists() }
    val resourceRoots = listOf(sharedResourcesDir, localResourcesDir)
        .distinctBy { it.absolutePath }
        .filter { it.exists() }
    val generatedComponentAtlasDir = layout.buildDirectory.dir("generated/sources/componentAtlas/main/java")

    apply(plugin = "net.neoforged.moddev")

    base {
        archivesName.set("$modId-$minecraftVersion")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(configuredJavaVersion))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }

    val runtimeJavaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(configuredRunJavaVersion))
        vendor.set(JvmVendorSpec.AZUL)
    }

    the<JavaPluginExtension>().sourceSets.named("main") {
        java.setSrcDirs(javaRoots + generatedComponentAtlasDir.get().asFile)
        resources.setSrcDirs(resourceRoots)
    }

    repositories {
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://cfa2.cursemaven.com") {
                    name = "CurseMaven"
                    metadataSources {
                        mavenPom()
                        artifact()
                        ignoreGradleMetadataRedirection()
                    }
                }
            }
            filter {
                includeGroup("curse.maven")
            }
        }
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForged Releases"
            content {
                includeGroupByRegex("net\\.neoforged(\\..+)?")
            }
        }
        maven("https://repo.spongepowered.org/maven") {
            name = "SpongePowered"
            content {
                includeGroup("org.spongepowered")
            }
        }
        maven("https://maven.theillusivec4.top/") {
            name = "Curios"
        }
    }

    dependencies {
        compileOnlyApi("org.jetbrains:annotations:24.1.0")
        annotationProcessor("org.jetbrains:annotations:24.1.0")
        compileOnly("org.spongepowered:mixin:0.8.5")
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }

    val rootDependenciesFile = rootProject.file("dependencies.gradle")
    if (rootDependenciesFile.exists()) {
        apply(from = rootDependenciesFile)
    }

    val localDependenciesFile = file("dependencies.gradle")
    if (localDependenciesFile.exists() && localDependenciesFile != rootDependenciesFile) {
        apply(from = localDependenciesFile)
    }

    apply(from = rootProject.file("gradle/scripts/platform-neoforge.gradle"))

    val generatedComponentAtlasPackage = "com.circulation.circulation_networks.gui.component.base"
    val generatedComponentAtlasFile = generatedComponentAtlasDir.map {
        it.file(generatedComponentAtlasPackage.replace('.', '/') + "/GeneratedComponentAtlasRegistration.java").asFile
    }
    val atlasComponentRelativePath = "assets/$modId/textures/gui/component"
    val componentAtlasInputDirs = resourceRoots
        .map { File(it, atlasComponentRelativePath) }
        .filter { it.exists() }

    val generateComponentAtlasRegistration = tasks.register("generateComponentAtlasRegistration") {
        group = "build setup"
        inputs.files(componentAtlasInputDirs)
        outputs.file(generatedComponentAtlasFile)

        doLast {
            writeIfChanged(
                generatedComponentAtlasFile.get(),
                buildGeneratedComponentAtlasRegistrationSource(
                    generatedComponentAtlasPackage,
                    collectPngResourceNames(resourceRoots, atlasComponentRelativePath)
                )
            )
        }
    }

    tasks.withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.withType<ProcessResources>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
    tasks.withType<JavaExec>().configureEach {
        javaLauncher.set(runtimeJavaLauncher)
    }
    tasks.withType<Test>().configureEach {
        javaLauncher.set(runtimeJavaLauncher)
    }
    tasks.named("compileJava") {
        dependsOn(generateComponentAtlasRegistration)
    }

    tasks.named<ProcessResources>("processResources") {
        val expansionMap = mapOf(
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_version" to modVersion,
            "mod_authors" to modAuthors.split(',').joinToString(", ") { it.trim() },
            "mod_description" to modDescription,
            "minecraft_version_range" to minecraftVersionRange,
            "loader_version_range" to loaderVersionRange,
            "neo_version_range" to neoVersionRange,
            "resource_pack_format" to resourcePackFormat
        )

        inputs.properties(expansionMap)
        exclude("mcmod.info", "META-INF/mods.toml")
        filesMatching(listOf("pack.mcmeta", "META-INF/neoforge.mods.toml", mixinConfigFile)) {
            expand(expansionMap)
        }
    }
}
