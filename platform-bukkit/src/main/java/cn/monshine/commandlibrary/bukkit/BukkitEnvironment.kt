package cn.monshine.commandlibrary.bukkit

import cn.monshine.commandlibrary.CommandEnvironment
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class BukkitEnvironment(private val plugin: JavaPlugin) : CommandEnvironment<CommandSender> {
    override fun sendMessage(sender: CommandSender, message: String) {
        sender.sendMessage(message)
    }

    override fun hasPermission(sender: CommandSender, perm: String): Boolean = sender.hasPermission(perm)
    override fun isOp(sender: CommandSender): Boolean = sender.isOp
    override fun isPlayer(sender: CommandSender): Boolean = sender is Player

    override fun format(text: String): String = ChatColor.translateAlternateColorCodes('&', text)

    override fun runAsync(task: () -> Unit) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
    }
}

