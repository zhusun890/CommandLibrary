package cn.monshine.commandlibrary.cli

import cn.monshine.commandlibrary.CommandRegistrar

class CLIRegistrar : CommandRegistrar<CliSender> {
    private data class Entry(
        val exec: (CliSender, String, List<String>) -> Boolean,
        val comp: (CliSender, String, List<String>) -> List<String>
    )
    private val entries = mutableMapOf<String, Entry>()

    override fun ensureRootRegistered(
        label: String,
        executor: (sender: CliSender, label: String, args: List<String>) -> Boolean,
        completer: (sender: CliSender, alias: String, args: List<String>) -> List<String>
    ) {
        entries.putIfAbsent(label, Entry(executor, completer))
    }

    fun executeLine(sender: CliSender, line: String): Boolean {
        val tokens = line.trim().removePrefix("/").split(" ").filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return false
        val label = tokens.first()
        val args = if (tokens.size > 1) tokens.subList(1, tokens.size) else emptyList()
        val command = entries[label] ?: return false
        return command.exec(sender, label, args)
    }
}

