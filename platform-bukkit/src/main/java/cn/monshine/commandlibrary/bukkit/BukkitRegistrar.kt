package cn.monshine.commandlibrary.bukkit

import cn.monshine.commandlibrary.CommandRegistrar
import cn.monshine.commandlibrary.transformer.PlayerTransformer
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin

class BukkitRegistrar(private val plugin: JavaPlugin) : CommandRegistrar<CommandSender> {
    private val commandMap: CommandMap
    private val registered = mutableSetOf<String>()

    init {
        val pm = Bukkit.getServer().pluginManager
        if (pm is SimplePluginManager) {
            val field = SimplePluginManager::class.java.getDeclaredField("commandMap")
            field.isAccessible = true
            commandMap = field[pm] as CommandMap
        } else {
            throw NoSuchFieldException("No commandMap found in your Spigot!")
        }
    }

    override fun ensureRootRegistered(
        label: String,
        executor: (sender: CommandSender, label: String, args: List<String>) -> Boolean,
        completer: (sender: CommandSender, alias: String, args: List<String>) -> List<String>
    ) {
        if (registered.add(label)) {
            commandMap.register(label, plugin.description.name.lowercase(), BukkitCommandBridge(label, executor, completer))
        }
    }

    override fun initialize(manager: cn.monshine.commandlibrary.CommandManager<CommandSender>) {
        super.initialize(manager) // basic types
        manager.registerTransformer(Player::class.java, PlayerTransformer())
    }
}
