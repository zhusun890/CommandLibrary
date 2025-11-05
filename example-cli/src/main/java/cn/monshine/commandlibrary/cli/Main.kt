package cn.monshine.commandlibrary.cli

import cn.monshine.commandlibrary.CommandManager

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val env = CLIEnvironment()
        val registrar = CLIRegistrar()
        val manager = CommandManager(env, registrar)

        manager.registerCommands(CliSampleCommands::class.java)

        val sender = CliSender(name = "cli", roles = setOf("op"))
        println("CLI ready. Type commands (e.g., '/demo'):")

        var input: String? = null

        do {
            input = readlnOrNull() ?: continue
            registrar.executeLine(sender, input)
        } while (input != "exit")
    }
}

