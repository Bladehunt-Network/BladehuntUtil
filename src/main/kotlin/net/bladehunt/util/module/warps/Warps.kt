package net.bladehunt.util.module.warps

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import net.bladehunt.util.BladehuntUtil
import net.bladehunt.util.module.IModule
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class Warps : IModule {
    override val name: String = "Warps"
    override val configName: String = "warps"

    private var config: ConfigurationSection? = null
    private var warps = hashMapOf<String,Warp>()

    override fun loadModule(plugin: BladehuntUtil): Boolean {
        config = getConfig(plugin)
        if (!config!!.getBoolean("enabled")) return false
        Bukkit.getLogger().info(Kolor.background(Kolor.foreground("   Loading warp module...   ",Color.BLACK), Color.CYAN))
        config!!.getConfigurationSection("locations").let { locations ->
            locations.getKeys(false).forEach { name ->
                val warpConf = locations.getConfigurationSection(name)
                warps[name] = Warp(
                    name,
                    warpConf.getString("world"),
                    warpConf.getDouble("x"),
                    warpConf.getDouble("y"),
                    warpConf.getDouble("z"),
                    if (warpConf.contains("yaw")) warpConf.getDouble("yaw").toFloat() else 0f,
                    if (warpConf.contains("pitch")) warpConf.getDouble("pitch").toFloat() else 0f,
                    if (warpConf.contains("permission")) warpConf.getString("permission") else null
                )
            }
        }
        WarpCommand(plugin,this)
        return true
    }
    fun getWarp(name: String): Warp? {
        return warps[name]
    }
    fun warpPlayer(player: Player, warp: Warp) {
        val plugin = BladehuntUtil.instance
        val audience = plugin.adventure().player(player)
        if (warp.permission != null && !player.hasPermission(warp.permission)) {
            audience.sendMessage(plugin.messageConfig.noPermission(player))
            return
        }
        if (!warp.location.chunk.isLoaded) {
            warp.location.chunk.load()
            object : BukkitRunnable() {
                override fun run() {
                    player.teleport(warp.location)
                    player.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1f, 1f)
                }
            }.runTaskLater(plugin,20)
            return
        }
        player.teleport(warp.location)
        player.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1f, 1f)
    }
    fun warpPlayer(player: Player, warp: String) {
        val plugin = BladehuntUtil.instance
        val audience = plugin.adventure().player(player)
        val w = getWarp(warp) ?: run {
            audience.sendMessage(plugin.messageConfig.warpNotFound(player))
            return
        }
        warpPlayer(player,w)
    }

    override fun unloadModule(plugin: BladehuntUtil) {
    }
}