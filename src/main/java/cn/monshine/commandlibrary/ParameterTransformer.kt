package cn.monshine.commandlibrary

import org.bukkit.command.CommandSender

interface ParameterTransformer<T> {
    /**
     * Transforms the specified argument to actual object using the provided command and arguments.
     *
     * @property sender The command sender
     * @property command The command wrapper
     * @property args The list of arguments
     * @property current The current argument
     * @return The transformed result, or null if unsuccessful.
     */
    fun transform(sender: CommandSender, command: CommandWrapper, args: List<String>, current: String): T?

    /**
     * Provides a list of possible completions for the given [current] string in the context of the specified [command].
     *
     * @property sender The sender
     * @property command The command wrapper
     * @property args The arguments
     * @property current The current argument
     * @return A list of possible completions.
     */
    fun tabComplete(sender: CommandSender, command: CommandWrapper, args: List<String>, current: String): List<String>
}