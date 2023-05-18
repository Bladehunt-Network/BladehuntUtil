package net.bladehunt.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object PluginCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        sender.hasPermission("bladehunt.command")
        val plugin = BladehuntUtil.instance
            plugin.adventure().sender(sender)
            .sendMessage(
                plugin.parseMM("<orange>BladehuntUtil <red>${plugin.description.version}\n" +
                        "Enabled Modules:" +
                        if (plugin.warpModule != null) "\n<gold>Warps" else "" +
                        if (plugin.chatDisplayModule != null) "\n<blue>Chat Display" else "" +
                        if (plugin.playerListModule != null) "\n<green>Player List" else "" +
                        if (plugin.displayNameModule != null) "\n<red>Display Name" else ""
                )
            )
        return true
    }
}