package com.lagradost

import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities

class FilelionsExtractor : ExtractorApi() {
    override val mainUrl: String = "Filelions"
    override val name: String = "https://filelions.live"
    override val requiresReferer: Boolean = true

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val refer = url
        val headers = mapOf(
            "Accept" to "*/*",
            "Accept-Language" to "en-US,en;q=0.5",
        )
        val document = app.get(url, headers = headers).document
        val urlScript = document.select("""script[jwplayer("vplayer").setup]""")
        val regexLink = Regex("""\"(https:\\\/\\\/[^"]*)""")
        val m3url = regexLink.find(urlScript.toString()).toString()
        return listOf(
            ExtractorLink(
                name,
                name,
                m3url,
                refer,
                Qualities.Unknown.value,
                true,
                headers = headers
            )
        )
    }
}