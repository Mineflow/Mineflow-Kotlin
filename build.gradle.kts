plugins {
    kotlin("jvm") version "1.4.31"
    id("net.minecrell.plugin-yml.nukkit") version "0.3.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "tokyo.aieuo"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven(url = "https://repo.nukkitx.com/main/")
}

dependencies {
    shadow(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
    implementation("io.github.g00fy2:versioncompare:1.4.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

nukkit {
    name = "Mineflow"
    main = "tokyo.aieuo.mineflow.Main"
    api = listOf("1.0.9")
    authors = listOf("aieuo")
    version = "2.0.2"
}