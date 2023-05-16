package net.bladehunt.util.module

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import net.bladehunt.util.BladehuntUtil
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scoreboard.DisplaySlot

class Scoreboard : IModule,Listener {
    override val name: String = "Scoreboard"
    override val configName: String = "scoreboard"

    var defaultBehavior: Boolean = true
    val excludedWorlds = arrayListOf<String>()

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        val config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        Bukkit.getLogger().info(Kolor.background(Kolor.foreground("   Loading scoreboard module...   ", Color.BLACK), Color.LIGHT_BLUE))

        defaultBehavior = config.getBoolean("default_world_behavior")
        excludedWorlds.addAll(config.getStringList("world_exclusions"))
        Bukkit.getPluginManager().registerEvents(this,plugin)
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective = scoreboard.registerNewObjective("dummy","dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.displayName = plugin.parseMM(config.getString("title")).examinableName()
        objective.getScore("").score = 0
        Bukkit.getOnlinePlayers().first().scoreboard
        return true
    }

    override fun unloadModule(plugin: BladehuntUtil) {
        HandlerList.unregisterAll(this)
    }
}