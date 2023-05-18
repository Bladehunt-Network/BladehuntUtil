package net.bladehunt.util.module

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import net.bladehunt.util.BladehuntUtil
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatDisplay : IModule,Listener {
    override val name: String = "Chat Display"
    override val configName: String = "chat_display"

    private var config: ConfigurationSection? = null
    private var format = "<%player_name%> {message}"

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        Bukkit.getPluginManager().registerEvents(this,plugin)
        config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        Bukkit.getLogger().info(Kolor.background(Kolor.foreground("   Loading chat display module...   ", Color.BLACK), Color.LIGHT_MAGENTA))
        format = config!!.getString("display")
        return true
    }

    override fun unloadModule(plugin: BladehuntUtil) {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        event.isCancelled = true
        val plugin = BladehuntUtil.instance
        plugin.adventure().filter { event.recipients.contains(it) }
            .sendMessage(
                plugin.parseMM(
                    plugin.replacePlaceholders(event.player,format)
                        .replace("{message}", event.message)
                )
            )
    }
}