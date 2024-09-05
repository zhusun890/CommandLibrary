package cn.monshine.commandlibrary.annotation

import cn.monshine.commandlibrary.ParamType

annotation class Param(
    val name: String,
    val defaultValue: String = "",
    val wildcard: Boolean = false,
    val type: ParamType = ParamType.REGULAR,
)