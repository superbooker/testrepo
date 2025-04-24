package com.lagradost

import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.app



open class Uqload3 : ExtractorApi() {
    override val name: String = "Uqload.to"
    override val mainUrl: String = "https://www.uqload.to"
    private val srcRegex = Regex("""sources:.\[(.*?)\]""")  // would be possible to use the parse and find src attribute
    override val requiresReferer = true


    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        with(app.get(url)) {  // raised error ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED (3003) is due to the response: "error_nofile"
            srcRegex.find(this.text)?.groupValues?.get(1)?.replace("\"", "")?.let { link ->
                return listOf(
                    ExtractorLink(
                        name,
                        name,
                        link,
                        url,
                        Qualities.Unknown.value,
                    )
                )
            }
        }
        return null
    }
}