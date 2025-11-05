package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.CommandTopicGetter
import java.lang.Integer.max
import java.lang.Integer.min

@Suppress("MemberVisibilityCanBePrivate")
class CommandManager<S : Any>(
    val env: CommandEnvironment<S>,
    private val registrar: CommandRegistrar<S>
) {
    val registeredCommandsTree = mutableMapOf<String, CommandTree<S>>()
    val labelTopics = mutableMapOf<String, CommandTopic>()
    val transformers = mutableMapOf<Class<*>, ParameterTransformer<S, *>>()

    init {
        registrar.initialize(this)
    }

    fun registerCommands(clazz: Class<*>) {
        val methods = clazz.declaredMethods.map { it.apply { isAccessible = true } }

        methods.filter { it.isAnnotationPresent(Command::class.java) }
            .forEach {
                buildHierarchy(CommandWrapper(this, it.getAnnotation(Command::class.java), it))
            }

        methods.filter { it.isAnnotationPresent(CommandTopicGetter::class.java) }
            .forEach {
                labelTopics[it.getAnnotation(CommandTopicGetter::class.java)!!.label] = it.invoke(null) as CommandTopic
            }
    }

    fun registerTransformer(type: Class<*>, transformer: ParameterTransformer<S, *>) {
        transformers[type] = transformer
        when (type) {
            Int::class.java -> transformers[Int::class.javaPrimitiveType!!] = transformer
            Boolean::class.java -> transformers[Boolean::class.javaPrimitiveType!!] = transformer
            Double::class.java -> transformers[Double::class.javaPrimitiveType!!] = transformer
            Short::class.java -> transformers[Short::class.javaPrimitiveType!!] = transformer
            Long::class.java -> transformers[Long::class.javaPrimitiveType!!] = transformer
            Char::class.java -> transformers[Char::class.javaPrimitiveType!!] = transformer
            Byte::class.java -> transformers[Byte::class.javaPrimitiveType!!] = transformer
        }
    }

    private fun buildHierarchy(wrapper: CommandWrapper<S>) {
        for (name in wrapper.command.names) {
            val segments = name.split(" ")
            val label = segments[0]
            val tree = registeredCommandsTree.computeIfAbsent(label) {
                CommandTree(label, null, true, this).also {
                    registrar.ensureRootRegistered(
                        label,
                        { sender, _, args -> executeRoot(sender, it, args) },
                        { sender, alias, args -> tabCompleteRoot(sender, it, alias, args) }
                    )
                }
            }

            if (segments.size == 1) {
                tree.defaultCommand = wrapper
            } else {
                var current = tree
                for (subLabel in segments.drop(1)) {
                    current = current.subCommands.computeIfAbsent(subLabel) { CommandTree(subLabel, current, false, this) }
                }
                current.defaultCommand = wrapper
            }
        }
    }

    fun executeRoot(sender: S, root: CommandTree<S>, args: List<String>): Boolean {
        val remaining = args.toMutableList()
        val command = root.resolve(remaining)
        if (command != null) {
            command.defaultCommand?.execute(sender, remaining.toTypedArray(), command.getFullName())
        } else {
            val actualCommands = root.getActualCommandNodes()
                .filter { it.defaultCommand?.command?.permission?.let { perm -> perm.isEmpty() || env.hasPermission(sender, perm) } ?: false }

            val topicAvailable = actualCommands
                .filter { it.resolveTopic() != null }
                .groupBy { it.resolveTopic()!! }

            val others = actualCommands.filter { it !in topicAvailable.values.flatten() }

            var index = 0
            for (entry in topicAvailable) {
                index++
                val commands = entry.value
                val topic = entry.key

                env.sendMessage(sender, env.format("&b${topic.name}"))
                topic.description.map { env.format("&7$it") }.forEach { env.sendMessage(sender, it) }
                for (subCommand in commands) showHelp(sender, subCommand)
                if (index != topicAvailable.size) env.sendMessage(sender, "")
            }

            for (subCommand in others) {
                if (topicAvailable.isNotEmpty()) env.sendMessage(sender, "")
                showHelp(sender, subCommand)
            }
        }
        return true
    }

    private fun showHelp(sender: S, subCommand: CommandTree<S>) {
        val wrapper = subCommand.defaultCommand ?: return
        val usage = "&f/${wrapper.generateUsage(decorate = false, label = subCommand.getFullName())} &7- ${wrapper.command.description}"
        env.sendMessage(sender, env.format(usage))
    }

    fun tabCompleteRoot(sender: S, parent: CommandTree<S>, alias: String, args: List<String>): MutableList<String> {
        val argsList = args.toMutableList()
        val command = if (argsList.isEmpty()) parent else parent.resolveTree(argsList)

        if (command != null) {
            return if (command.defaultCommand == null) {
                val last = if (args.isEmpty()) "" else args.last()
                command.subCommands.keys.filter { args.isEmpty() || it.startsWith(last) }.toMutableList()
            } else {
                mutableListOf(command.label)
            }
        } else {
            val remaining = args.toMutableList()
            val subTree = parent.resolve(remaining)
            if (subTree != null) {
                val subCommand = subTree.defaultCommand ?: return mutableListOf()
                if (subCommand.requiredArgsType.isEmpty()) {
                    return mutableListOf()
                } else {
                    val nonFlags = subCommand.requiredArgsType.filter { it.getAnnotation(cn.monshine.commandlibrary.annotation.Param::class.java).type != ParamType.FLAG }
                    val idx = max(0, min(remaining.size - 1, nonFlags.size - 1))
                    val relevantParameter = nonFlags[idx]
                    val transformer = transformers[relevantParameter.type] as? ParameterTransformer<S, *> ?: return mutableListOf()
                    @Suppress("UNCHECKED_CAST")
                    return (transformer as ParameterTransformer<S, Any>).tabComplete(sender, subCommand, remaining, remaining.last()).toMutableList()
                }
            }
            return mutableListOf()
        }
    }
}
