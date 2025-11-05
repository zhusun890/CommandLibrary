package cn.monshine.commandlibrary.annotation

annotation class Command(
    val names: Array<String>,
    val description: String = "",
    val permission: String = "",
    val playerOnly: Boolean = true,
    val async: Boolean = false,
)

