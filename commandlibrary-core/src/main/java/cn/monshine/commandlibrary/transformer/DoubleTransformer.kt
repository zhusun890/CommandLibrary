package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer

class DoubleTransformer<S : Any> : ParameterTransformer<S, Double> {
    override fun transform(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): Double? = try {
        current.toDouble()
    } catch (_: NumberFormatException) {
        command.manager.env.sendMessage(sender, command.manager.env.format("&cPlease provide a valid number!"))
        null
    }

    override fun tabComplete(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): List<String> = emptyList()
}

