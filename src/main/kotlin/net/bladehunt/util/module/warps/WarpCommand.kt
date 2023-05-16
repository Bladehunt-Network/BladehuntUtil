package net.bladehunt.util.module.warps

import net.bladehunt.util.BladehuntUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpCommand(private val plugin: BladehuntUtil, private val module: Warps) : CommandExecutor {
    init {
        plugin.getCommand("warp").executor = this
    }
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(plugin.messageConfig.notPlayer)
            return true
        }
        val audience = plugin.adventure().player(sender)
        if (args.isEmpty()) {
            audience.sendMessage(Component.text("Please provide a warp! Use /warp or /warp (warp)", NamedTextColor.RED))
            return true
        }
        if (args.size > 1) {
            audience.sendMessage(Component.text("Too many arguments! Use /warp or /warp (warp)", NamedTextColor.RED))
            return true
        }
        module.warpPlayer(sender,args[0])
        return true
    }
}