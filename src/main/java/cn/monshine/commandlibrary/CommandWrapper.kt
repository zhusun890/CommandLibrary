package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.Param
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.concurrent.CompletableFuture

class CommandWrapper(val command: Command, private val handle: Method) {
    /**
     * The type of arguments that are required for [handle].
     */
    val requiredArgsType = handle.parameters.toList().subList(1, handle.parameters.toList().size).toList()

    /**
     * Invokes the command with the specified sender and arguments.
     *
     * @property sender The command sender
     * @param args Variable number of string arguments
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @Throws(IndexOutOfBoundsException::class)
    private fun invoke(sender: CommandSender, vararg args: String) {
        val mappedArgs = mutableListOf<Any>()

        var argIndex = 0
        for (parameter in requiredArgsType) {
            validateParameter(parameter.getAnnotation(Param::class.java), parameter)

            val annotation = parameter.getAnnotation(Param::class.java)!!
            if (annotation.type == ParamType.FLAG) {
                mappedArgs.add(args.contains("-${annotation.name}"))
                if (args.isNotEmpty() && args[argIndex] == "-${annotation.name}") {
                    argIndex++ // skip this
                }
            } else {
                val arg = if (args.size - 1 < argIndex && annotation.defaultValue.isNotEmpty()) {
                    annotation.defaultValue
                } else if (annotation.wildcard) {
                    args.toList().subList(argIndex, args.size).joinToString(separator = " ")
                } else {
                    args[argIndex++]
                }

                val transformed = CommandHandler.transformers[parameter.type]!!.transform(
                    sender,
                    this,
                    args.toList(),
                    arg
                ) ?: return

                mappedArgs.add(transformed)
            }
        }

        if (Player::class.java.isAssignableFrom(handle.parameters[0].type)) {
            if (sender !is Player) {
                throw IllegalArgumentException("The command allows console to use, but it's handle requires a player parameter.")
            }
        }
        mappedArgs.addFirst(sender)

        handle.invoke(null, *mappedArgs.toTypedArray())
    }

    /**
     * Validates the parameter [param] with its corresponding annotation.
     *
     * @property param The parameter to validate
     * @throws IllegalArgumentException If no @Param annotation is found, if a flag parameter has an invalid type, or if a transformer for the parameter's type hasn't been registered.
     */
    private fun validateParameter(annotation: Param?, param: Parameter) {
        if (annotation == null) throw IllegalArgumentException("No @Param annotation found for param ${param.name} at ${handle.name}.")

        if (annotation.type == ParamType.FLAG && param.type != Boolean::class.java && param.type != Boolean::class.javaPrimitiveType)
            throw IllegalArgumentException("Only boolean parameters could be a flag.")

        if (param.type !in CommandHandler.transformers.keys)
            throw IllegalArgumentException("Transformer for ${param.type.simpleName} hasn't been registered!")
    }

    /**
     * Executes the command with the specified arguments.
     *
     * @property sender The CommandSender to execute the command for
     * @property args The command arguments
     * @property label The command label
     * @return True if the command was executed successfully, false otherwise
     */
    fun execute(sender: CommandSender, args: Array<out String>, label: String): Boolean {
        if (sender !is Player && command.playerOnly) {
            sender.sendMessage("&cOnly players can execute this command.".colorize())
            return false
        }

        if (command.permission.isNotEmpty()) {
            if (command.permission.equals("op", ignoreCase = true)) {
                if (!sender.isOp) {
                    sender.sendMessage("&cNo permission".colorize())
                    return false
                }
            } else {
                if (!sender.hasPermission(command.permission)) {
                    sender.sendMessage("&cNo permission.".colorize())
                    return false
                }
            }
        }

        try {
            if (command.async) {
                CompletableFuture.runAsync {
                    invoke(sender, *args)
                }
            } else {
                invoke(sender, *args)
            }
            return true
        } catch (_: IndexOutOfBoundsException) {
            sender.sendMessage(generateUsage(label = label))
            return false
        }
    }

    /**
     * Generates a usage message for the specified command.
     *
     * @property decorate Whether to colorize the output
     * @property label The label of the command
     * @return A string representing the usage message
     */
    fun generateUsage(decorate: Boolean = true, label: String): String {
        return "${if (decorate) "&cUsage: /" else ""}${label} ${
            requiredArgsType.joinToString(separator = " ") {
                val annotation = it.getAnnotation(Param::class.java)
                val inner =
                    "${if (annotation.type == ParamType.FLAG) "-" else ""}${annotation.name}${if (annotation.wildcard) "..." else ""}"

                if (annotation.defaultValue.isNotEmpty() || annotation.type == ParamType.FLAG) {
                    "[$inner]"
                } else {
                    "<$inner>"
                }
            }
        }".colorize()
    }

}