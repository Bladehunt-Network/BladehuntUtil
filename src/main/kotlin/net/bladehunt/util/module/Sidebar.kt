package net.bladehunt.util.module

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import fr.mrmicky.fastboard.FastBoard
import net.bladehunt.util.BladehuntUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.scheduler.BukkitRunnable

class Sidebar : IModule,Listener {
    override val name: String = "Sidebar"
    override val configName: String = "sidebar"
    var defaultBehavior: Boolean = true
        private set
    val excludedWorlds = arrayListOf<String>()
    var joinDelay = 20
        private set
    var updateInterval = 40
        private set

    private var title = "Bladehunt Utils"
    private val lines = arrayListOf<String>()
    private val fastBoards = hashMapOf<Player,FastBoard>()

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        val config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        plugin.logger.info(Kolor.background(Kolor.foreground("   Loading sidebar module...   ", Color.BLACK), Color.LIGHT_GREEN))
        defaultBehavior = config.getBoolean("default_world_behavior")
        excludedWorlds.addAll(config.getStringList("world_exclusions"))
        joinDelay = config.getInt("join_delay")
        updateInterval = config.getInt("update_interval")
        title = config.getString("title")
        lines.addAll(config.getStringList("lines"))

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
                    fastBoards[player]?.delete()
                    fastBoards.remove(player)
                    return
                }
                if (fastBoards[player] == null) fastBoards[player] = FastBoard(player)
                updateSidebar(plugin,player)
            }
        }.runTaskTimerAsynchronously(plugin,joinDelay.toLong(), updateInterval.toLong())
    }
    override fun unloadModule(plugin: BladehuntUtil) {
        Bukkit.getOnlinePlayers().forEach { player ->
            val audience = plugin.adventure().player(player)
            audience.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty())
        }
        HandlerList.unregisterAll(this)
    }
    fun updateSidebar(plugin: BladehuntUtil, player: Player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            fastBoards[player]?.let{
                it.updateTitle(
                    LegacyComponentSerializer.legacySection().serialize(
                        plugin.parseMM(plugin.replacePlaceholders(player,title))
                    )
                )
                it.updateLines(lines.map { LegacyComponentSerializer.legacySection().serialize(
                    plugin.parseMM(plugin.replacePlaceholders(player,it))
                ) })
            }
        }
    }
    private fun getBehavior(world: World): Boolean {
        if (defaultBehavior) return !excludedWorlds.contains(world.name)
        return excludedWorlds.contains(world.name)
    }
}