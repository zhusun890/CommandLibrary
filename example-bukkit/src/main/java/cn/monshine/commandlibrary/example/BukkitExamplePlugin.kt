package cn.monshine.commandlibrary.example

import cn.monshine.commandlibrary.bukkit.BukkitCommands
import org.bukkit.plugin.java.JavaPlugin

class BukkitExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        // One-line install: create manager and register your command classes
        BukkitCommands.install(this, TestCommand::class.java)
    }
}

