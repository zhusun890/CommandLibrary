package cn.monshine.commandlibrary.cli

data class CliSender(val name: String = "user", val roles: Set<String> = emptySet()) {
    fun send(msg: String) {
        println("[$name] $msg")
    }
}

