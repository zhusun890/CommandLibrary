package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.test.TestCommand
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class CommandLibrary : JavaPlugin() {
    override fun onEnable() {
        instance = this

        CommandHandler.registerCommands(TestCommand::class.java)
    }

    override fun onDisable() {

    }

    companion object {
        @JvmStatic
        lateinit var instance: CommandLibrary
    }
}