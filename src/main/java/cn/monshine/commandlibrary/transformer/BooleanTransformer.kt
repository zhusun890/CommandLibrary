package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer
import org.bukkit.command.CommandSender

class BooleanTransformer : ParameterTransformer<Boolean> {
    override fun transform(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): Boolean {
        return current.toBoolean()
    }

    override fun tabComplete(
        sender: CommandSender,
        command: CommandWrapper,
        args: List<String>,
        current: String
    ): List<String> {
        return listOf("true", "false")
    }
}