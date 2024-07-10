import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.regex.Pattern

buildscript {
    repositories {
        gradlePluginPortal();
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

class NettyEpollTransformer : Transformer {
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    private var extractedNettyNative: Path? = null

    override fun canTransformResource(element: FileTreeElement): Boolean {
        return element.name.endsWith("libnetty_transport_native_epoll_x86_64.so")
    }

    override fun transform(context: TransformerContext) {
        this.extractedNettyNative = Files.createTempFile("nuvotifer_build", ".so")
        Files.copy(context.`is`, this.extractedNettyNative, StandardCopyOption.REPLACE_EXISTING)
    }

    override fun hasTransformedResource(): Boolean {
        return this.extractedNettyNative != null
    }

    override fun modifyOutputStream(jos: org.apache.tools.zip.ZipOutputStream?, preserveFileTimestamps: Boolean) {
        val entry =
            org.apache.tools.zip.ZipEntry("META-INF/native/libcom_vexsoftware_votifier_netty_transport_native_epoll_x86_64.so")
        entry.time = TransformerContext.getEntryTimestamp(preserveFileTimestamps, entry.time)
        jos?.putNextEntry(entry)
        Files.copy(this.extractedNettyNative, jos)
        Files.delete(this.extractedNettyNative)
    }
}

dependencies {
    implementation(project(":nuvotifier-api"))
    implementation(project(":nuvotifier-common"))
    implementation(project(":nuvotifier-bukkit"))
    implementation(project(":nuvotifier-bungeecord"))
    implementation(project(":nuvotifier-sponge"))
    implementation(project(":nuvotifier-velocity"))
}

tasks.shadowJar {
    //archiveBaseName."nuvotifier"
    relocate("io.netty", "com.vexsoftware.votifier.io.netty")
    relocate("org.json", "com.vexsoftware.votifier.json")
    relocate(SimpleRelocator("com.google.code", "com.vexsoftware.votifier.google.code", listOf(), listOf("com.vexsoftware.votifier.sponge")))
    relocate("com.google.gson", "com.vexsoftware.votifier.google.gson")
    relocate("org.apache.commons.io", "com.vexsoftware.votifier.commons.io")

    //transform<NettyEpollTransformer>()
}

artifacts {
    tasks.shadowJar.get().outputs.files.forEach {
        archives(it) {
            builtBy(tasks.shadowJar)
        }
    }
}

val artifactPattern: Pattern = Pattern.compile("/(.*)-([0-9]+\\.)*[0-9]+(-SNAPSHOT)?\\.jar/")

val linkLatest = task("linkLatest") {
    dependsOn(tasks.shadowJar)
    doLast {
        tasks.shadowJar.get().outputs.files.forEach {
            println(it.absolutePath)
            val noVerMatcher = artifactPattern.matcher(it.absolutePath)
            if (!noVerMatcher.matches())
                throw IllegalStateException("wtf")

            val noVer = Paths.get(noVerMatcher.group(1) + ".jar")
            val versnd = it.toPath()
            Files.copy(versnd, noVer, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

tasks.build {
    dependsOn(linkLatest)
}
