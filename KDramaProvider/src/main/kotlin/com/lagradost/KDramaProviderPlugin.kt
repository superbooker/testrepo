
package com.lagradost

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

@CloudstreamPlugin
class KDramaProviderPlugin: Plugin() {
    val kDramaProvider = KDramaProvider()
    override fun load(context: Context) {
        // All providers should be added in this manner. Please don't edit the providers list directly.
        registerMainAPI(KDramaProvider())
        registerExtractorAPI(FilelionsExtractor())
    }
}
