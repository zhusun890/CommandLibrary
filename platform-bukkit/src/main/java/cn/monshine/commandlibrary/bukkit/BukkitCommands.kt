package cn.monshine.commandlibrary.bukkit

import cn.monshine.commandlibrary.CommandManager
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Facade for Bukkit developers: create/install the command framework without
 * touching internal abstractions.
 */
object BukkitCommands {
    @JvmStatic
    fun create(plugin: JavaPlugin): CommandManager<CommandSender> {
        val env = BukkitEnvironment(plugin)
        val registrar = BukkitRegistrar(plugin)
        return CommandManager(env, registrar)
    }

    @JvmStatic
    fun install(plugin: JavaPlugin, vararg commandClasses: Class<*>): CommandManager<CommandSender> {
        val manager = create(plugin)
        commandClasses.forEach { manager.registerCommands(it) }
        return manager
    }

    @JvmStatic
    fun register(manager: CommandManager<CommandSender>, vararg commandClasses: Class<*>) {
        commandClasses.forEach { manager.registerCommands(it) }
    }
}

