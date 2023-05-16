package net.bladehunt.util.module.warps

import org.bukkit.Bukkit
import org.bukkit.Location

data class Warp(
    val name: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val permission: String? = null
) {
    val location get() = Location(Bukkit.getWorld(world),x,y,z,yaw,pitch)
}