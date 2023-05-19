package net.bladehunt.util.module

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import net.bladehunt.util.BladehuntUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.scheduler.BukkitRunnable

class PlayerList : IModule, Listener {
    override val name: String = "Player List"
    override val configName: String = "player_list"

    var defaultBehavior: Boolean = true
        private set
    val excludedWorlds = arrayListOf<String>()
    var joinDelay = 20
        private set
    var updateInterval = 40
        private set

    private var header = ""
    private var footer = ""

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        val config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        plugin.logger.info(Kolor.background(Kolor.foreground("   Loading player list module...   ", Color.BLACK), Color.LIGHT_BLUE))
        header = config.getString("header")
        footer = config.getString("footer")
        defaultBehavior = config.getBoolean("default_world_behavior")
        excludedWorlds.addAll(config.getStringList("world_exclusions"))
        joinDelay = config.getInt("join_delay")
        updateInterval = config.getInt("update_interval")

        Bukkit.getPluginManager().registerEvents(this,plugin)
        return true
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val plugin = BladehuntUtil.instance
        val player = event.player
        object : BukkitRunnable() {
            override fun run() {
                if (!getBehavior(player.world)) {
                    return
                }
                updatePlayerList(plugin,player)
            }
        }.runTaskTimerAsynchronously(plugin,joinDelay.toLong(), updateInterval.toLong())
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (event.from.world == event.to.world) return
        val player = event.player
        val plugin = BladehuntUtil.instance
        val audience = plugin.adventure().player(player)
        if (!getBehavior(event.to.world)) audience.sendPlayerListHeaderAndFooter(Component.empty(),Component.empty())
        else updatePlayerList(plugin,player)
    }
    override fun unloadModule(plugin: BladehuntUtil) {
        Bukkit.getOnlinePlayers().forEach { player ->
            val audience = plugin.adventure().player(player)
            audience.sendPlayerListHeaderAndFooter(Component.empty(),Component.empty())
        }
        HandlerList.unregisterAll(this)
    }
    fun updatePlayerList(plugin: BladehuntUtil, player: Player) {
        val audience = plugin.adventure().player(player)
        audience.sendPlayerListHeaderAndFooter(
            plugin.parseMM(plugin.replacePlaceholders(player,header)),
            plugin.parseMM(plugin.replacePlaceholders(player,footer))
        )
    }
    private fun getBehavior(world: World): Boolean {
        if (defaultBehavior) return !excludedWorlds.contains(world.name)
        return excludedWorlds.contains(world.name)
    }
}