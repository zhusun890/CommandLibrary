package cn.monshine.commandlibrary

/**
 * Platform abstraction for messaging, permissions, and async execution.
 */
interface CommandEnvironment<S : Any> {
    fun sendMessage(sender: S, message: String)
    fun hasPermission(sender: S, perm: String): Boolean
    fun isOp(sender: S): Boolean = false
    fun isPlayer(sender: S): Boolean = false

    /**
     * Platform-specific formatting (e.g., color codes).
     * Default is identity.
     */
    fun format(text: String): String = text

    /**
     * Run a task asynchronously on platform's scheduler/thread pool.
     */
    fun runAsync(task: () -> Unit)
}

