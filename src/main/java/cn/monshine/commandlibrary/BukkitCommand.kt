package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.annotation.Param
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import kotlin.math.max
import kotlin.math.min

class BukkitCommand(@Suppress("MemberVisibilityCanBePrivate") val parent: CommandTree) : Command(parent.label) {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val remaining = args.toMutableList()

        val command = parent.resolve(remaining)
        if (command != null) {
            command.defaultCommand?.execute(sender, remaining.toTypedArray(), command.getFullName())
        } else {
            val actualCommands = parent.getActualCommandNodes()

            val topicAvailable = actualCommands
                .filter { it.resolveTopic() != null }
                .groupBy { it.resolveTopic()!! }

            val others = actualCommands.filter { it !in topicAvailable.values.flatten() }

            var index = 0
            for (entry in topicAvailable) {
                index++
                val commands = entry.value
                val topic = entry.key

                sender.sendMessage("&b${topic.name}".colorize())
                topic.description.map { "&7$it".colorize() }.forEach(sender::sendMessage)

                for (subCommand in commands) {
                    showHelp(sender, subCommand)
                }

                if (index != topicAvailable.size) {
                    sender.sendMessage("")
                }
            }

            for (subCommand in others) {
                if (topicAvailable.isNotEmpty()) {
                    sender.sendMessage("")
                }
                showHelp(sender, subCommand)
            }
        }

        return true
    }

    private fun showHelp(sender: CommandSender, subCommand: CommandTree) {
        if (subCommand.defaultCommand != null) {
            val usage = "&f/${subCommand.defaultCommand!!.generateUsage(decorate = false, label = subCommand.getFullName())} &7- ${subCommand.defaultCommand!!.command.description}".colorize()
            sender.sendMessage(usage)
        }
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val argsList = args.toListIgnoreEmptyString() // ignore empty strings, thanks to bukkit

        val command = if (argsList.isEmpty()) parent else parent.resolveTree(argsList)

        if (command != null) {
            return if (command.defaultCommand == null) command.subCommands.keys.filter { argsList.isEmpty() || it.startsWith(argsList.last()) }
                .toMutableList() else mutableListOf(command.label)
        } else {
            val remaining = args.toListIgnoreEmptyString()
            val subTree = parent.resolve(remaining)
            if (subTree != null) {
                val subCommand = subTree.defaultCommand ?: return mutableListOf()
                if (subCommand.requiredArgsType.isEmpty()) {
                    return mutableListOf()
                } else {
                    val relevantParameter = subCommand.requiredArgsType
                        .filter { it.getAnnotation(Param::class.java).type != ParamType.FLAG }[max(0, min(remaining.size - 1, subCommand.requiredArgsType.size - 1))]

                    val transformer = CommandHandler.transformers[relevantParameter.type] ?: return mutableListOf()
                    return transformer.tabComplete(sender, subCommand, remaining, remaining.last()).toMutableList()
                }
            }

            return mutableListOf()
        }
    }
}