package cn.monshine.commandlibrary.bukkit

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class BukkitCommandBridge(
    label: String,
    private val executor: (sender: CommandSender, label: String, args: List<String>) -> Boolean,
    private val completer: (sender: CommandSender, alias: String, args: List<String>) -> List<String>
) : Command(label) {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor(sender, label, args.filter { it.isNotEmpty() })
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return completer(sender, alias, args.toList()).toMutableList()
    }
}
