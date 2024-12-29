plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.github.yreader"
version = "1.0.0"


// 配置仓库源
repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies") }
}

// 配置 Java 版本
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// 配置 IntelliJ IDEA 插件开发环境
intellij {
    version.set("2023.1.5")
    type.set("IC")
    plugins.set(listOf())
    updateSinceUntilBuild.set(true)
}

// 配置插件打包选项
tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("233.*")
        
        pluginDescription.set("""
            A plug-in for reading novels in the IDEA editor, which supports paginated reading, content hiding and other functions.
        """.trimIndent())
        
        changeNotes.set("""
            Initial release of the plugin:
            - 功能1
            - 功能2
        """.trimIndent())
        
        version.set(project.version.toString())
        pluginId.set("com.github.yreader")
    }

    signPlugin {
        enabled = false
    }

    publishPlugin {
        enabled = false
    }
}

dependencies {
    // 添加其他依赖
    // implementation("some.group:artifact:version")
}