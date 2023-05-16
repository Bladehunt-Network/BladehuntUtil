package net.bladehunt.util

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
import me.clip.placeholderapi.PlaceholderAPI
import net.bladehunt.util.module.ChatDisplay
import net.bladehunt.util.module.DisplayName
import net.bladehunt.util.module.PlayerList
import net.bladehunt.util.module.warps.Warps
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class BladehuntUtil : JavaPlugin() {
    companion object {
        private var _instance: BladehuntUtil? = null
        val instance: BladehuntUtil get() = _instance!!
    }

    private var _messageConfig: MessageConfig? = null
    val messageConfig: MessageConfig get() = _messageConfig!!

    private var adventure: BukkitAudiences? = null
    private var papiEnabled = false

    fun replacePlaceholders(player: Player, str: String): String {
        return if (papiEnabled) PlaceholderAPI.setPlaceholders(player,str) else str
    }
    fun parseMM(str: String): Component {
        return MiniMessage.miniMessage().deserialize(str)
    }
    fun adventure(): BukkitAudiences {
        checkNotNull(adventure) { "Tried to access Adventure when the plugin was disabled!" }
        return adventure!!
    }
    var warpModule: Warps? = null
        private set
    var playerListModule: PlayerList? = null
        private set
    var chatDisplayModule: ChatDisplay? = null
        private set
    var displayNameModule: DisplayName? = null
        private set

    override fun onEnable() {
        _instance = this
        this.adventure = BukkitAudiences.create(this)
        papiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null

        Bukkit.getLogger().info(
            Kolor.background(
                Kolor.foreground("   Initializing BladehuntUtil...   ",Color.BLACK),
                Color.RED)
        )
        saveDefaultConfig()
        Warps().let { module ->
            module.loadModule(this).let { enabled ->
                warpModule = module
            }
        }
        PlayerList().let { module ->
            module.loadModule(this).let { enabled ->
                playerListModule = module
            }
        }
        ChatDisplay().let { module ->
            module.loadModule(this).let { enabled ->
                chatDisplayModule = module
            }
        }
        DisplayName().let { module ->
            module.loadModule(this).let { enabled ->
                displayNameModule = module
            }
        }
        this._messageConfig = MessageConfig(config.getConfigurationSection("messages"),this)
    }
    override fun onDisable() {
        if(this.adventure != null) {
            this.adventure?.close()
            this.adventure = null
        }
        _instance = null
        papiEnabled = false
    }
}