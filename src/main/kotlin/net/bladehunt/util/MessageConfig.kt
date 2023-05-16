package net.bladehunt.util

import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class MessageConfig(private val configurationSection: ConfigurationSection, private val plugin: BladehuntUtil) {
    // Static Messages
    val notPlayer = configurationSection.getString("not_player")

    fun noPermission(player: Player): Component {
        return plugin.parseMM(plugin.replacePlaceholders(player,configurationSection.getString("no_permission")))
    }
    fun warpNotFound(player: Player): Component {
        return plugin.parseMM(plugin.replacePlaceholders(player,configurationSection.getString("warp_not_found")))
    }
}