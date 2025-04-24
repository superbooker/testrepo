package com.lagradost


import android.text.Editable
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import android.util.Log
import okio.ByteString.Companion.decodeBase64
import org.jsoup.nodes.Element
import java.util.Base64


class KDramaProvider : MainAPI() {
    override var mainUrl = "https://kdrama.best/" 
    override var name = "KDrama"
    override val supportedTypes = setOf(TvType.AsianDrama)
    override val hasQuickSearch = false
    override var lang = "fr"


    // enable this when your provider has a main page
    override val hasMainPage = true

    // this function gets called when you search for something
    override suspend fun search(query: String): List<SearchResponse> {
        val link = "$mainUrl/?s=$query" // search'
        val document =
            app.post(link).document // app.get() permet de télécharger la page html avec une requete HTTP (get)
        val results = document.select("div.listupd > > article.bs")

        val allresultshome =
            results.mapNotNull { article ->  // avec mapnotnull si un élément est null, il sera automatiquement enlevé de la liste
                article.toSearchResponse()
            }
        return allresultshome
    }
    private fun Element.toSearchResponse(): SearchResponse {
        val posterUrl = fixUrl(select("img.ts-post-image").attr("src"))
        val title = select("div.tt.tts").text()
        val link = select("a.tip").attr("href")

        return newTvSeriesSearchResponse(
                name = title,
                url = link,
                type = TvType.AsianDrama,

                ) {
                this.posterUrl = posterUrl   
            }
    }
    override suspend fun load(url: String): LoadResponse{
        val soup = app.get(url).document
        val title = soup.selectFirst("h1.entry-title")!!.text().toString()
        val description = soup.selectFirst("div.entry-content")!!.text().toString()
        val poster = soup.selectFirst("div.thumb > img")?.attr("src")
        val listEpisode = soup.select("div.eplister")
        var episode = listOf<Episode>()
        //Populate Episode with list Episode
        episode = listEpisode[0].takeEpisode()
        return newAnimeLoadResponse(title, url, TvType.AsianDrama){
            this.posterUrl = poster.toString()
            this.plot = description
            if(episode.isNotEmpty()) addEpisodes(DubStatus.Subbed, episode)
        }
    }
    private fun Element.takeEpisode(): List<Episode>{
        return this.select("a").map { a ->
            val epNum = a.select("div.epl-num").text().toInt()
            val epTitle = a.select("div.epl-title").text()
            val epUrl = a?.attr("href").toString()
            Episode(
                loadLinkData(epUrl).toJson(),
                epTitle.replace("Vostfr", ""), 
                null, 
                epNum, 
                null,
            )
        }
    }
    data class loadLinkData(
        val embedUrl: String,
    )
    override suspend fun loadLinks( 
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit,
    ): Boolean {
        val parsedData =  tryParseJson<loadLinkData>(data)
        val url = parsedData?.embedUrl ?: return false
        val document = app.get(url).document
        val results = document.select("select.mirror")

        results.apmap { infoEpisode ->
            val playerUrlBase64 = infoEpisode.select("option").attr("value")
            val decodedPlayerUrl = playerUrlBase64?.decodeBase64()?.utf8()

            val regex = """SRC="([^"]+)"""".toRegex()
            val matchResult = regex.find(decodedPlayerUrl.toString())
            val playerUrl = matchResult?.groupValues?.apmap { url ->
                loadExtractor(
                    httpsify(url),
                    url,
                    subtitleCallback
                ) { link ->
                    callback.invoke(
                        ExtractorLink(
                            link.source,
                            link.name + "",
                            link.url,
                            link.referer,
                            getQualityFromName("HD"),
                            true,
                            link.headers,
                            link.extractorData
                        )
                    )
                }
            }
        }
        return true;
    }
    override val mainPage = mainPageOf(
        Pair("/series/?status=&type=&order=update", "Dernier Episodes"),
    )
    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = mainUrl + request.data + page
        val document = app.get(url).document
        val movies = document.select("div.listupd > > article.bs")
        val home =
            movies.map { article ->  
                article.toSearchResponse()
            }
        return newHomePageResponse(request.name, home)
    }
}

