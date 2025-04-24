// use an integer for version numbers
version = 1
dependencies {
    implementation("androidx.core:core:1.7.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
}

cloudstream {
    language = "fr"
    // All of these properties are optional, you can safely remove them

     description = "KDrama propose des drama Corréen de qualité avec une grande rapidité de publication"
     authors = listOf("AirbnbEcoPlus")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 3 // will be 3 if unspecified
    tvTypes = listOf(
        "AsianDrama",
    )

    iconUrl = ""
    requiresResources = false
}
