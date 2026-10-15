pluginManagement {
    repositories {
        google { content { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.google.*"); includeGroupByRegex("androidx.*") } }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Yesid SDK artifacts (io.yesid:*): public, no credentials.
        maven { url = uri("https://maven.yesid.io/releases") }
        // The document reader and face API that the SDK builds on (without these, dependency resolution fails).
        maven { url = uri("https://maven.regulaforensics.com/RegulaDocumentReader") }
        maven { url = uri("https://maven.regulaforensics.com/RegulaDocumentReaderWebClient") }
        maven { url = uri("https://jitpack.io") }
        // Only for testing an SDK build that is not published yet: ./gradlew ... -PuseMavenLocal=true
        if (providers.gradleProperty("useMavenLocal").orNull == "true") mavenLocal()
    }
}
rootProject.name = "yesid-sample"
include(":app")
