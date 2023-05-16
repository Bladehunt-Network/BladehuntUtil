package net.bladehunt.util.module

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import net.bladehunt.util.BladehuntUtil
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class DisplayName : IModule,Listener {
    override val name: String = "Display Name"
    override val configName: String = "display_names"
    private var config: ConfigurationSection? = null
    private var format = "%player_name%"

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        Bukkit.getLogger().info(Kolor.background(Kolor.foreground("   Loading display name module...   ", Color.BLACK), Color.LIGHT_GREEN))
        Bukkit.getPluginManager().registerEvents(this,plugin)
        format = config!!.getString("display")
        return true
    }
    @EventHandler
    private fun joinListener(event: PlayerJoinEvent) {
        val name = LegacyComponentSerializer.legacySection().serialize(
            BladehuntUtil.instance.parseMM(
                BladehuntUtil.instance.replacePlaceholders(event.player,format)
            )
        )
        event.player.displayName = name
        event.player.playerListName = name
        event.player.sendMessage(format)
    }
    override fun unloadModule(plugin: BladehuntUtil) {
        HandlerList.unregisterAll(this)
    }
}