package cn.monshine.commandlibrary.cli

import cn.monshine.commandlibrary.CommandTopic
import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.CommandTopicGetter
import cn.monshine.commandlibrary.annotation.Param

object CliSampleCommands {
    @JvmStatic
    @CommandTopicGetter(label = "demo")
    fun topic(): CommandTopic = CommandTopic("CLI Demo", listOf("Examples for CLI platform"))

    @JvmStatic
    @Command(names = ["demo echo"], description = "Echo text")
    fun echo(sender: CliSender, @Param(name = "text", wildcard = true) text: String) {
        sender.send("echo: $text")
    }

    @JvmStatic
    @Command(names = ["demo sum"], description = "Sum two integers")
    fun sum(sender: CliSender, @Param(name = "a") a: Int, @Param(name = "b") b: Int) {
        sender.send("sum = ${a + b}")
    }
}

