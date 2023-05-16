package net.bladehunt.util.module

import net.bladehunt.util.BladehuntUtil
import org.bukkit.configuration.ConfigurationSection

interface IModule {
    val name: String
    val configName: String
    fun getConfig(plugin: BladehuntUtil): ConfigurationSection? {
        return plugin.config.getConfigurationSection("modules").getConfigurationSection(configName)
    }
    fun loadModule(plugin: BladehuntUtil): Boolean
    fun unloadModule(plugin: BladehuntUtil)
}