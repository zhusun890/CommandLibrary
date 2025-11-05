package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer

class BooleanTransformer<S : Any> : ParameterTransformer<S, Boolean> {
    override fun transform(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): Boolean = current.toBoolean()
    override fun tabComplete(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): List<String> =
        listOf("true", "false").filter { it.startsWith(current, true) }
}

