package net.bladehunt.util.module

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import net.bladehunt.util.BladehuntUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable

class PlayerList : IModule {
    override val name: String = "Player List"
    override val configName: String = "player_list"

    var defaultBehavior: Boolean = true
    val excludedWorlds = arrayListOf<String>()

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        val config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        Bukkit.getLogger().info(Kolor.background(Kolor.foreground("   Loading player list module...   ", Color.BLACK), Color.LIGHT_BLUE))
        val header = config.getString("header")
        val footer = config.getString("footer")
        defaultBehavior = config.getBoolean("default_world_behavior")
        excludedWorlds.addAll(config.getStringList("world_exclusions"))
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { player ->
                    val audience = plugin.adventure().player(player)
                    audience.sendPlayerListHeaderAndFooter(
                        plugin.parseMM(plugin.replacePlaceholders(player,header)),
                        plugin.parseMM(plugin.replacePlaceholders(player,footer))
                    )
                }
            }
        }.runTaskTimerAsynchronously(plugin,1,1)
        return true
    }

    override fun unloadModule(plugin: BladehuntUtil) {
        Bukkit.getOnlinePlayers().forEach { player ->
            val audience = plugin.adventure().player(player)
            audience.sendPlayerListHeaderAndFooter(Component.empty(),Component.empty())
        }
    }
    private fun getBehavior(world: World): Boolean {
        if (defaultBehavior) return !excludedWorlds.contains(world.name)
        return excludedWorlds.contains(world.name)
    }
}