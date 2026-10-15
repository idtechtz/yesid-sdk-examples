package io.yesid.sample

import io.yesid.licensing.transport.YesidEnvironment

/** Settings from `Config.local` (see Config.example), compiled into BuildConfig by app/build.gradle.kts. */
object Config {
    val publishableKey: String get() = BuildConfig.PUBLISHABLE_KEY
    val consumerUsername: String get() = BuildConfig.CONSUMER_USERNAME
    val sessionUrl: String get() = BuildConfig.SESSION_URL
    val ocrLicense: String get() = BuildConfig.OCR_LICENSE
    val faceLicense: String get() = BuildConfig.FACE_LICENSE

    val environment: YesidEnvironment
        get() = if (BuildConfig.ENVIRONMENT.equals("production", ignoreCase = true)) YesidEnvironment.PRODUCTION else YesidEnvironment.SANDBOX

    /** The names of the settings that are still empty; empty list = ready to run. */
    fun missing(): List<String> = buildList {
        if (publishableKey.isBlank()) add("PUBLISHABLE_KEY")
        if (consumerUsername.isBlank()) add("CONSUMER_USERNAME")
        if (sessionUrl.isBlank()) add("SESSION_URL")
        if (ocrLicense.isBlank()) add("OCR_LICENSE")
        if (faceLicense.isBlank()) add("FACE_LICENSE")
    }
}
