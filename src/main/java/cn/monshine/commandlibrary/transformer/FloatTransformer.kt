package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer
import cn.monshine.commandlibrary.colorize
import org.bukkit.command.CommandSender

class FloatTransformer : ParameterTransformer<Float> {
    override fun transform(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): Float? {
        try {
            return current.toFloat()
        } catch (_: NumberFormatException) {
            sender.sendMessage("&cPlease provide a valid integer number!".colorize())
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