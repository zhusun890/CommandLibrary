package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer
import org.bukkit.command.CommandSender

class StringTransformer : ParameterTransformer<String> {
    override fun transform(sender: CommandSender, command: CommandWrapper, args: List<String>, current: String): String {
        return current
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