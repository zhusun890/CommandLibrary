package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.Param
import cn.monshine.commandlibrary.annotation.CommandTopicGetter
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class DummySender(
    val name: String = "tester",
    val perms: Set<String> = emptySet(),
    val player: Boolean = true,
) {
    val out: MutableList<String> = mutableListOf()
}

class DummyEnv : CommandEnvironment<DummySender> {
    override fun sendMessage(sender: DummySender, message: String) { sender.out.add(message) }
    override fun hasPermission(sender: DummySender, perm: String): Boolean = sender.perms.contains(perm)
    override fun isOp(sender: DummySender): Boolean = sender.perms.contains("op")
    override fun isPlayer(sender: DummySender): Boolean = sender.player
    override fun format(text: String): String = text
    override fun runAsync(task: () -> Unit) { task() }
}

class DummyRegistrar : CommandRegistrar<DummySender> {
    override fun ensureRootRegistered(
        label: String,
        executor: (sender: DummySender, label: String, args: List<String>) -> Boolean,
        completer: (sender: DummySender, alias: String, args: List<String>) -> List<String>
    ) { /* not needed for direct manager.executeRoot testing */ }
}

object TestCommands {
    @JvmStatic
    @Command(names = ["greet"], description = "Greet someone", playerOnly = false)
    fun greet(sender: DummySender, @Param(name = "name") name: String) {
        sender.out.add("hello $name")
    }

    @JvmStatic
    @Command(names = ["ban"], description = "ban", playerOnly = false)
    fun ban(sender: DummySender,
            @Param(name = "s", type = ParamType.FLAG) silent: Boolean,
            @Param(name = "target") target: String,
            @Param(name = "reason", wildcard = true) reason: String) {
        sender.out.add("ban silent=$silent target=$target reason=$reason")
    }

    @JvmStatic
    @Command(names = ["level"], description = "level", playerOnly = false)
    fun level(sender: DummySender, @Param(name = "level", defaultValue = "10") lvl: Int) {
        sender.out.add("lvl=$lvl")
    }

    @JvmStatic
    @Command(names = ["secret"], description = "secret", permission = "perm.secret", playerOnly = false)
    fun secret(sender: DummySender) { sender.out.add("secret-ok") }

    @JvmStatic
    @Command(names = ["playerOnly"], description = "p", playerOnly = true)
    fun playerOnly(sender: DummySender) { sender.out.add("player-ok") }
}

object TreeCommands {
    @JvmStatic
    @Command(names = ["root a"], description = "A", playerOnly = false)
    fun a(sender: DummySender) { sender.out.add("a") }

    @JvmStatic
    @Command(names = ["root b"], description = "B", playerOnly = false)
    fun b(sender: DummySender) { sender.out.add("b") }
}

class CoreFrameworkTest {
    private fun newManager(): CommandManager<DummySender> = CommandManager(DummyEnv(), DummyRegistrar())

    @Test
    fun defaultTransformersAreRegistered() {
        val manager = newManager()
        assertTrue(manager.transformers.containsKey(String::class.java))
        assertTrue(manager.transformers.containsKey(Boolean::class.java))
        assertTrue(manager.transformers.containsKey(Int::class.java))
        assertTrue(manager.transformers.containsKey(Float::class.java))
        assertTrue(manager.transformers.containsKey(Double::class.java))
    }

    @Test
    fun executeSimpleCommand() {
        val manager = newManager()
        manager.registerCommands(TestCommands::class.java)
        val root = manager.registeredCommandsTree["greet"]!!
        val s = DummySender()
        manager.executeRoot(s, root, listOf("Bob"))
        assertContains(s.out.joinToString("|"), "hello Bob")
    }

    @Test
    fun flagsWildcardAndDefaults() {
        val manager = newManager()
        manager.registerCommands(TestCommands::class.java)

        val s1 = DummySender()
        manager.executeRoot(s1, manager.registeredCommandsTree["ban"]!!, listOf("-s", "Alice", "griefing"))
        assertTrue(s1.out.any { it.contains("silent=true") && it.contains("target=Alice") && it.contains("reason=griefing") })

        val s2 = DummySender()
        manager.executeRoot(s2, manager.registeredCommandsTree["level"]!!, emptyList())
        assertTrue(s2.out.any { it.contains("lvl=10") })
    }

    @Test
    fun permissionAndPlayerOnly() {
        val manager = newManager()
        manager.registerCommands(TestCommands::class.java)

        val sNoPerm = DummySender(perms = emptySet())
        manager.executeRoot(sNoPerm, manager.registeredCommandsTree["secret"]!!, emptyList())
        assertTrue(sNoPerm.out.any { it.contains("No permission") })

        val sConsole = DummySender(player = false)
        manager.executeRoot(sConsole, manager.registeredCommandsTree["playerOnly"]!!, emptyList())
        assertTrue(sConsole.out.any { it.contains("Only players") })
    }

    @Test
    fun tabCompletionForSubcommands() {
        val manager = newManager()
        manager.registerCommands(TreeCommands::class.java)
        val root = manager.registeredCommandsTree["root"]!!
        val s = DummySender()
        val options = manager.tabCompleteRoot(s, root, "root", emptyList())
        assertEquals(setOf("a", "b"), options.toSet())

        val optionsFiltered = manager.tabCompleteRoot(s, root, "root", listOf("a"))
        assertEquals(listOf("a"), optionsFiltered)
    }
}

