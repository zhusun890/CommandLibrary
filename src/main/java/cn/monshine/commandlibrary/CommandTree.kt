@file:Suppress("KDocUnresolvedReference")

package cn.monshine.commandlibrary

@Suppress("MemberVisibilityCanBePrivate")
class CommandTree(val label: String, val parent: CommandTree?) {
    /**
     * A map of subcommands and their corresponding command trees.

     * @property key The name of a subcommand
     * @property value The command tree for the specified [key]
     */
    val subCommands = mutableMapOf<String, CommandTree>()

    /**
     * The default command wrapper.
     */
    var defaultCommand: CommandWrapper? = null

    /**
     * The internal topic name for the current label.
     */
    val internalTopic
        get() = CommandHandler.labelTopics[getFullName(virtualLabelOnly = true)]

    /**
     * Resolves the command tree based on the given [remaining] arguments.

     * @property remaining The list of remaining arguments to resolve.
     * @return The resolved command tree, or null if no matching sub-command is found.
     */
    fun resolve(remaining: MutableList<String>): CommandTree? {
        if (subCommands.isEmpty() && defaultCommand != null) return this
        if (remaining.isEmpty() || remaining.first() !in subCommands) {
            return null
        }

        val label = remaining.removeFirst()
        return subCommands[label]?.resolve(remaining)
    }

    /**
     * Recursively resolves the command tree by traversing the given [remaining] list of labels.
     *
     * @property remaining The list of labels to resolve
     * @return The resolved command tree, or this instance if the list is empty.
     */
    fun resolveTree(remaining: MutableList<String>): CommandTree? {
        if (remaining.isEmpty()) {
            return this
        }

        val label = remaining.removeFirst()
        return subCommands[label]?.resolveTree(remaining)
    }

    /**
     * Fills a list with actual command nodes and returns it.

     * @return A list of actual command nodes.
     */
    fun getActualCommandNodes(): List<CommandTree> {
        val dummy = mutableListOf<CommandTree>()
        fillActualCommands(dummy)
        return dummy
    }

    /**
     * Recursively fills the actual commands into [commands].

     * @property defaultCommand The default command, or null if not set.
     * @property subCommands The sub-commands.
     */
    private fun fillActualCommands(commands: MutableList<CommandTree>) {
        if (defaultCommand != null) {
            commands.add(this)
            return
        }

        subCommands.forEach { it.value.fillActualCommands(commands) }
    }

    /**
     * Returns the full name of this command, including its ancestors.
     *
     * @property virtualLabelOnly If true, only include the virtual label in the result.
     * @return The full name as a string.
     */
    fun getFullName(virtualLabelOnly: Boolean = false): String {
        val trees = mutableListOf<CommandTree>()

        var iteration = parent ?: return label
        while (iteration.parent != null) {
            trees.addFirst(iteration)
            iteration = iteration.parent!!
        }
        trees.addFirst(iteration)

        if (defaultCommand != null && !virtualLabelOnly) trees.addLast(this)

        return trees.joinToString(separator = " ") { it.label }
    }

    /**
     * Resolves the topic by recursively searching up the hierarchy.
     *
     * @return The resolved topic, or null if not found.
     */
    fun resolveTopic(): CommandTopic? {
        if (internalTopic != null) return internalTopic

        var iteration = parent ?: return null
        while (iteration.internalTopic == null && iteration.parent != null) {
            iteration = iteration.parent!!
        }

        return iteration.internalTopic
    }
}