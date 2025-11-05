package cn.monshine.commandlibrary.transformer

import cn.monshine.commandlibrary.CommandWrapper
import cn.monshine.commandlibrary.ParameterTransformer

class StringTransformer<S : Any> : ParameterTransformer<S, String> {
    override fun transform(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): String = current
    override fun tabComplete(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): List<String> = emptyList()
}

