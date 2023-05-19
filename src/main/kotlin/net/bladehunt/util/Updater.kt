package net.bladehunt.util

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.Bukkit


object Updater {
    private const val url = "https://api.github.com/repos/Bladehunt-Network/BladehuntUtil/releases/latest"
    private val client = OkHttpClient()
    private val gson = Gson()
    fun checkUpdates(version: String) = runBlocking {
        BladehuntUtil.instance.logger.info(Kolor.foreground("Checking for updates...", Color.DARK_GRAY))
        launch {
            val request = Request.Builder()
                .url(url)
                .header("Content-Type","applicataion/json")
                .build()
            val call = client.newCall(request)
            val body = call.execute().body ?: return@launch
            val json = gson.fromJson(body.string(), JsonObject::class.java)
            if (!json.has("assets")) return@launch
            val assets = json.getAsJsonArray("assets")
            if (assets.size() == 0) return@launch
            for (asset in assets) {
                val url = asset.asJsonObject["browser_download_url"].asString
                if (url.endsWith("all.jar")) {
                    if (url.split("/").last().removeSuffix("-all.jar").removePrefix("BladehuntUtil-") == version) {
                        BladehuntUtil.instance.logger.info(Kolor.foreground("BladehuntUtil is up to date!", Color.GREEN))
                        return@launch
                    }
                    BladehuntUtil.instance.logger.info(Kolor.foreground("BladehuntUtil has an update! Download it at $url", Color.RED))
                    break
                }
            }

        }

    }
}