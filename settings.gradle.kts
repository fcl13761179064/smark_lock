import org.gradle.internal.impldep.org.junit.platform.engine.support.descriptor.FileSource.from


pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven (url ="https://gitee.com/liuchaoya/libcommon/raw/master/repository/")
        maven(url="https://jitpack.io")
        maven( url ="https://maven.aliyun.com/repository/central/")
        maven( url ="https://maven.aliyun.com/repository/jcenter/")
        maven( url ="https://maven.aliyun.com/repository/google/")
        maven( url ="https://maven.aliyun.com/repository/public")

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    versionCatalogs {
           files("./toml/fmlibs.versions.toml")
    }
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven (url ="https://gitee.com/liuchaoya/libcommon/raw/master/repository/")
        maven(url="https://jitpack.io")
        maven( url ="https://maven.aliyun.com/repository/central/")
        maven( url ="https://maven.aliyun.com/repository/jcenter/")
        maven( url ="https://maven.aliyun.com/repository/google/")
        maven( url ="https://maven.aliyun.com/repository/public")
    }
}


rootProject.name = "smart_lock"
include(":app")
include(":commons")
include(":photoselector")
include(":seriallibrary")
include(":room_database")
include(":libuvccamera")
