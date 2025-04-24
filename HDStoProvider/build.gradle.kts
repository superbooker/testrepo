// use an integer for version numbers
version = 3


cloudstream {
    language = "fr"
    // All of these properties are optional, you can safely remove them

    description = "HDS.to - Film streaming, Voirfilms, Film en streaming, regarder des films en streaming gratuitement sur HDS, Voir films streaming gratuit."
    authors = listOf("zzikozz")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 3 // will be 3 if unspecified
    tvTypes = listOf("Movie")

    iconUrl = "https://www2.hds-streaming.to/favicon.ico"
}