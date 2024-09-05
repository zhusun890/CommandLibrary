package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer
import cn.monshine.commandlibrary.colorize
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerTransformer : ParameterTransformer<Player> {
    override fun transform(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): Player? {
        val result = Bukkit.getPlayer(current)

        if (result == null) {
            sender.sendMessage("&cNo player with name $current found.".colorize())
        }

        return result
    }

    override fun tabComplete(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): List<String> {
        return Bukkit.getOnlinePlayers().map { it.name }
    }
}