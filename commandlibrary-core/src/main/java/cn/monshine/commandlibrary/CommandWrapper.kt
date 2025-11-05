package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.Param
import java.lang.IndexOutOfBoundsException
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class CommandWrapper<S : Any>(val manager: CommandManager<S>, val command: Command, private val handle: Method) {
    val requiredArgsType: List<Parameter> = handle.parameters.toList().subList(1, handle.parameters.toList().size).toList()

    private fun invoke(sender: S, vararg args: String) {
        val mappedArgs = mutableListOf<Any>()
        var argIndex = 0
        for (parameter in requiredArgsType) {
            validateParameter(parameter.getAnnotation(Param::class.java), parameter)
            val annotation = parameter.getAnnotation(Param::class.java)!!
            if (annotation.type == ParamType.FLAG) {
                mappedArgs.add(args.contains("-${annotation.name}"))
                if (args.isNotEmpty() && argIndex < args.size && args[argIndex] == "-${annotation.name}") {
                    argIndex++
                }
            } else {
                val arg = if (args.size - 1 < argIndex && annotation.defaultValue.isNotEmpty()) {
                    annotation.defaultValue
                } else if (annotation.wildcard) {
                    args.toList().subList(argIndex, args.size).joinToString(separator = " ")
                } else {
                    args[argIndex++]
                }

                val transformer = manager.transformers[parameter.type]
                    ?: throw IllegalStateException("No transformer registered for ${parameter.type.simpleName}")

                @Suppress("UNCHECKED_CAST")
                val transformed = (transformer as ParameterTransformer<S, Any>).transform(
                    sender,
                    this,
                    args.toList(),
                    arg
                ) ?: return
                mappedArgs.add(transformed)
            }
        }

        val firstParam = handle.parameters[0].type
        if (!firstParam.isAssignableFrom(sender.javaClass)) {
            manager.env.sendMessage(sender, manager.env.format("&cThis command requires a different sender type."))
            return
        }
        mappedArgs.add(0, sender)
        handle.invoke(null, *mappedArgs.toTypedArray())
    }

    private fun validateParameter(annotation: Param?, param: Parameter) {
        if (annotation == null) throw IllegalArgumentException("No @Param annotation found for param ${param.name} at ${handle.name}.")
        if (annotation.type == ParamType.FLAG && param.type != java.lang.Boolean::class.java && param.type != java.lang.Boolean.TYPE)
            throw IllegalArgumentException("Only boolean parameters could be a flag.")
        if (param.type !in manager.transformers.keys)
            throw IllegalArgumentException("Transformer for ${param.type.simpleName} hasn't been registered!")
    }

    fun execute(sender: S, args: Array<out String>, label: String): Boolean {
        if (command.playerOnly && !manager.env.isPlayer(sender)) {
            manager.env.sendMessage(sender, manager.env.format("&cOnly players can execute this command."))
            return false
        }

        if (command.permission.isNotEmpty()) {
            if (command.permission.equals("op", ignoreCase = true)) {
                if (!manager.env.isOp(sender)) {
                    manager.env.sendMessage(sender, manager.env.format("&cNo permission"))
                    return false
                }
            } else {
                if (!manager.env.hasPermission(sender, command.permission)) {
                    manager.env.sendMessage(sender, manager.env.format("&cNo permission."))
                    return false
                }
            }
        }

        try {
            if (command.async) {
                manager.env.runAsync { invoke(sender, *args) }
            } else {
                invoke(sender, *args)
            }
            return true
        } catch (_: IndexOutOfBoundsException) {
            manager.env.sendMessage(sender, generateUsage(label = label))
            return false
        }
    }

    fun generateUsage(decorate: Boolean = true, label: String): String {
        val raw = "${if (decorate) "&cUsage: /" else ""}${label} ${
            requiredArgsType.joinToString(separator = " ") {
                val annotation = it.getAnnotation(Param::class.java)
                val inner = "${if (annotation.type == ParamType.FLAG) "-" else ""}${annotation.name}${if (annotation.wildcard) "..." else ""}"
                if (annotation.defaultValue.isNotEmpty() || annotation.type == ParamType.FLAG) "[$inner]" else "<$inner>"
            }
        }"
        return manager.env.format(raw)
    }
}
