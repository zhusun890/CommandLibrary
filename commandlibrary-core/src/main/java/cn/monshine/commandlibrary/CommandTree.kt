@file:Suppress("KDocUnresolvedReference")

package cn.monshine.commandlibrary

@Suppress("MemberVisibilityCanBePrivate")
class CommandTree<S : Any>(val label: String, val parent: CommandTree<S>?, val rootNode: Boolean = false, private val manager: CommandManager<S>) {
    val subCommands = mutableMapOf<String, CommandTree<S>>()
    var defaultCommand: CommandWrapper<S>? = null

    private val internalTopic
        get() = manager.labelTopics[getFullName(virtualLabelOnly = true)]

    fun resolve(remaining: MutableList<String>): CommandTree<S>? {
        if (subCommands.isEmpty() && defaultCommand != null) return this
        if (remaining.isEmpty() && defaultCommand != null && rootNode) return this

        if (remaining.isEmpty() || remaining.first() !in subCommands) {
            val rootCommand = defaultCommand
            return if (rootNode && rootCommand != null) this else null
        }

        val label = remaining.removeFirst()
        return subCommands[label]?.resolve(remaining)
    }

    fun resolveTree(remaining: MutableList<String>): CommandTree<S>? {
        if (remaining.isEmpty()) return this
        val label = remaining.removeFirst()
        return subCommands[label]?.resolveTree(remaining)
    }

    fun getActualCommandNodes(): List<CommandTree<S>> {
        val dummy = mutableListOf<CommandTree<S>>()
        fillActualCommands(dummy)
        return dummy
    }

    private fun fillActualCommands(commands: MutableList<CommandTree<S>>) {
        if (defaultCommand != null) {
            commands.add(this)
            return
        }
        subCommands.forEach { it.value.fillActualCommands(commands) }
    }

    fun getFullName(virtualLabelOnly: Boolean = false): String {
        val trees = mutableListOf<CommandTree<S>>()

        var iteration = parent ?: return label
        while (iteration.parent != null) {
            trees.addFirst(iteration)
            iteration = iteration.parent
        }
        trees.addFirst(iteration)

        if (defaultCommand != null && !virtualLabelOnly) trees.addLast(this)
        return trees.joinToString(separator = " ") { it.label }
    }

    fun resolveTopic(): CommandTopic? {
        if (internalTopic != null) return internalTopic
        var iteration = parent ?: return null
        while (iteration.internalTopic == null && iteration.parent != null) {
            iteration = iteration.parent
        }
        return iteration.internalTopic
    }
}

