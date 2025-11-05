package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerTransformer : ParameterTransformer<CommandSender, Player> {
    override fun transform(
        sender: CommandSender,
        command: CommandWrapper<CommandSender>,
        args: List<String>,
        current: String
    ): Player? {
        val result = Bukkit.getPlayer(current)
        if (result == null) {
            command.manager.env.sendMessage(sender, command.manager.env.format("&cNo player with name $current found."))
        }
        return result
    }

    override fun tabComplete(
        sender: CommandSender,
        command: CommandWrapper<CommandSender>,
        args: List<String>,
        current: String
    ): List<String> = Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(current, ignoreCase = true) }
}

