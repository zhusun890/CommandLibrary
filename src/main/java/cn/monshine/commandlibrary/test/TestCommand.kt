package cn.monshine.commandlibrary.test

import cn.monshine.commandlibrary.CommandTopic
import cn.monshine.commandlibrary.ParamType
import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.CommandTopicGetter
import cn.monshine.commandlibrary.annotation.Param
import org.bukkit.entity.Player

object TestCommand {
    @JvmStatic
    @CommandTopicGetter(label = "test")
    fun testTopic(): CommandTopic {
        return CommandTopic(name = "Test Commands", description = listOf("This is line1", "This is line2"))
    }

    @JvmStatic
    @Command(names = ["test a"], permission = "op", description = "t", playerOnly = true)
    fun testA(sender: Player) {
        sender.sendMessage("testa")
    }

    @JvmStatic
    @Command(names = ["test b"], permission = "op", description = "t", playerOnly = true)
    fun testB(sender: Player, @Param(name = "i", type = ParamType.FLAG) flag: Boolean) {
        sender.sendMessage("testb")
    }

    @JvmStatic
    @CommandTopicGetter(label = "test c")
    fun testCTopic(): CommandTopic {
        return CommandTopic(name = "Test Commands (C)", description = listOf("This is line1", "js mei ma"))
    }

    @JvmStatic
    @Command(names = ["test c d e"], permission = "op", description = "t", playerOnly = true)
    fun testC(sender: Player, @Param(name = "a") flag: Boolean, @Param(name = "c") flag2: Boolean) {
        sender.sendMessage("testc")
    }

    @JvmStatic
    @Command(names = ["ban"], permission = "op", description = "Ban someone", playerOnly = true)
    fun ban(sender: Player, @Param(name = "s", type = ParamType.FLAG) silent: Boolean, @Param(name = "target") target: Player, @Param(name = "reason", wildcard = true) reason: String) {
        sender.sendMessage("Attempt to ban ${target.name} with: silent=$silent, reason=$reason")
    }
}