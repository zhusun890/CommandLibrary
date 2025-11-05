package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer

class IntTransformer<S : Any> : ParameterTransformer<S, Int> {
    override fun transform(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): Int? = try {
        current.toInt()
    } catch (_: NumberFormatException) {
        command.manager.env.sendMessage(sender, command.manager.env.format("&cPlease provide a valid integer number!"))
        null
    }

    override fun tabComplete(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): List<String> = emptyList()
}

