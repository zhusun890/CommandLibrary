package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer
import cn.monshine.commandlibrary.colorize
import org.bukkit.command.CommandSender

class DoubleTransformer : ParameterTransformer<Double> {
    override fun transform(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): Double? {
        try {
            return current.toDouble()
        } catch (_: NumberFormatException) {
            sender.sendMessage("&cPlease provide a valid number!".colorize())
            return null
        }
    }

    override fun tabComplete(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): List<String> {
        return emptyList()
    }

}