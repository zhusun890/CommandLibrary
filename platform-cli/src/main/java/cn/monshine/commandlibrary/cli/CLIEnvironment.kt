package cn.monshine.commandlibrary.cli

import cn.monshine.commandlibrary.CommandEnvironment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CLIEnvironment(
    private val asyncExecutor: ExecutorService = Executors.newCachedThreadPool(),
    private val ansi: Boolean = true
) : CommandEnvironment<CliSender> {

    override fun sendMessage(sender: CliSender, message: String) {
        sender.send(message)
    }

    override fun hasPermission(sender: CliSender, perm: String): Boolean {
        if (perm.isEmpty()) return true
        return sender.roles.contains(perm)
    }

    override fun isOp(sender: CliSender): Boolean = sender.roles.contains("op")
    override fun isPlayer(sender: CliSender): Boolean = true
    override fun format(text: String): String {
        if (!text.contains('&')) return text

        val color = mapOf(
            '0' to "\u001B[30m", // black
            '1' to "\u001B[34m", // dark blue
            '2' to "\u001B[32m", // dark green
            '3' to "\u001B[36m", // dark aqua
            '4' to "\u001B[31m", // dark red
            '5' to "\u001B[35m", // dark purple
            '6' to "\u001B[33m", // gold
            '7' to "\u001B[37m", // gray
            '8' to "\u001B[90m", // dark gray
            '9' to "\u001B[94m", // blue
            'a' to "\u001B[92m", // green
            'b' to "\u001B[96m", // aqua
            'c' to "\u001B[91m", // red
            'd' to "\u001B[95m", // light purple
            'e' to "\u001B[93m", // yellow
            'f' to "\u001B[97m"  // white
        )
        val style = mapOf(
            'l' to "\u001B[1m", // bold
            'n' to "\u001B[4m", // underline
            'o' to "\u001B[3m", // italic
            'm' to "\u001B[9m"  // strikethrough
        )

        val reset = "\u001B[0m"
        val sb = StringBuilder()
        var i = 0
        var used = false
        while (i < text.length) {
            val ch = text[i]
            if (ch == '&' && i + 1 < text.length) {
                val code = text[i + 1].lowercaseChar()
                when {
                    color.containsKey(code) -> {
                        if (ansi) sb.append(color.getValue(code))
                        used = used || ansi
                        i += 2; continue
                    }
                    style.containsKey(code) -> {
                        if (ansi) sb.append(style.getValue(code))
                        used = used || ansi
                        i += 2; continue
                    }
                    code == 'r' -> {
                        if (ansi) sb.append(reset)
                        used = used || ansi
                        i += 2; continue
                    }
                    else -> { /* fallthrough, treat as literal */ }
                }
            }
            sb.append(ch)
            i++
        }
        if (ansi && used) sb.append(reset)
        return sb.toString()
    }

    override fun runAsync(task: () -> Unit) {
        asyncExecutor.submit { task() }
    }
}
