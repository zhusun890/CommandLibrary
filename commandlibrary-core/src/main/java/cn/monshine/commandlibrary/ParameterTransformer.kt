package cn.monshine.commandlibrary

interface ParameterTransformer<S : Any, T> {
    /**
     * Transform the string argument [current] into an instance of [T]. Return null to abort execution.
     */
    fun transform(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): T?

    /**
     * Provide tab-completion candidates for the argument currently being typed.
     */
    fun tabComplete(sender: S, command: CommandWrapper<S>, args: List<String>, current: String): List<String>
}

