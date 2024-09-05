@file:Suppress("KDocUnresolvedReference")

package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.annotation.Command
import cn.monshine.commandlibrary.annotation.CommandTopicGetter
import cn.monshine.commandlibrary.transformer.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.entity.Player
import org.bukkit.plugin.SimplePluginManager

object CommandHandler {
    /**
     * A map of registered command trees keyed by their names.

     * @property registeredCommandsTree The map of registered commands
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val registeredCommandsTree = mutableMapOf<String, CommandTree>()

    /**
     * A map of topics to labels.
     *
     * @property key The topic name
     * @property value The corresponding command topic
     */
    val labelTopics = mutableMapOf<String, CommandTopic>()

    /**
     * A map of parameter transformers keyed by their respective class types.

     * @property key The class type
     * @property value The transformer for the specified class type
     */
    val transformers = mutableMapOf<Class<*>, ParameterTransformer<*>>()

    private var commandMap: CommandMap

    init {
        if (Bukkit.getServer().pluginManager is SimplePluginManager) {
            val field = SimplePluginManager::class.java.getDeclaredField("commandMap")
            field.isAccessible = true
            commandMap = field[Bukkit.getServer().pluginManager] as CommandMap
        } else {
            throw NoSuchFieldException("No commandMap found in your Spigot!")
        }

        registerTransformer(Boolean::class.java, BooleanTransformer())
        registerTransformer(String::class.java, StringTransformer())
        registerTransformer(Int::class.java, IntTransformer())
        registerTransformer(Float::class.java, FloatTransformer())
        registerTransformer(Double::class.java, DoubleTransformer())
        registerTransformer(Player::class.java, PlayerTransformer())
    }

    /**
     * Registers commands and command topics from the specified class.
     *
     * @property clazz The class to register commands and topics from
     */
    @JvmStatic
    fun registerCommands(clazz: Class<*>) {
        val methods = clazz.declaredMethods.map {
            it.apply { isAccessible = true }
        }
        methods.filter { it.isAnnotationPresent(Command::class.java) }
            .forEach {
                buildHierarchy(CommandWrapper(it.getAnnotation(Command::class.java), it))
            }

        methods.filter { it.isAnnotationPresent(CommandTopicGetter::class.java) }
            .forEach {
                labelTopics[it.getAnnotation(CommandTopicGetter::class.java)!!.label] = it.invoke(null) as CommandTopic
            }
    }

    /**
     * Builds the command hierarchy based on the provided [wrapper].
     *
     * @property wrapper The command wrapper.
     */
    @JvmStatic
    private fun buildHierarchy(wrapper: CommandWrapper) {
        for (name in wrapper.command.names) {
            val segments = name.split(" ")

            val label = segments[0]
            val tree = registeredCommandsTree.computeIfAbsent(label) {
                CommandTree(label, null).also {
                    commandMap.register(label, "", BukkitCommand(it))
                }
            }

            if (segments.size == 1) {
                tree.defaultCommand = wrapper
            } else {
                // parse until last label
                var current = tree
                for (subLabel in segments.drop(1)) {
                    current = current.subCommands.computeIfAbsent(subLabel) { CommandTree(subLabel, current) }
                }
                current.defaultCommand = wrapper
            }
        }
    }

    /**
     * Registers a [ParameterTransformer] for the specified type.
     *
     * @property type The class of the parameter to transform.
     * @param transformer The transformer to register.
     */
    @JvmStatic
    fun registerTransformer(type: Class<*>, transformer: ParameterTransformer<*>) {
        transformers[type] = transformer
        when (type) {
            Int::class.java -> {
                transformers[Int::class.javaPrimitiveType!!] = transformer
            }

            Boolean::class.java -> {
                transformers[Boolean::class.javaPrimitiveType!!] = transformer
            }

            Double::class.java -> {
                transformers[Double::class.javaPrimitiveType!!] = transformer
            }

            Short::class.java -> {
                transformers[Short::class.javaPrimitiveType!!] = transformer
            }

            Long::class.java -> {
                transformers[Long::class.javaPrimitiveType!!] = transformer
            }

            Char::class.java -> {
                transformers[Char::class.javaPrimitiveType!!] = transformer
            }

            Byte::class.java -> {
                transformers[Byte::class.javaPrimitiveType!!] = transformer
            }
        }
    }
}