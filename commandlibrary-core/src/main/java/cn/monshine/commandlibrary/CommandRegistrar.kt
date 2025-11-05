package cn.monshine.commandlibrary

import cn.monshine.commandlibrary.transformer.BooleanTransformer
import cn.monshine.commandlibrary.transformer.DoubleTransformer
import cn.monshine.commandlibrary.transformer.FloatTransformer
import cn.monshine.commandlibrary.transformer.IntTransformer
import cn.monshine.commandlibrary.transformer.StringTransformer

/**
 * Platform abstraction for registering root commands.
 */
interface CommandRegistrar<S : Any> {
    fun ensureRootRegistered(
        label: String,
        executor: (sender: S, label: String, args: List<String>) -> Boolean,
        completer: (sender: S, alias: String, args: List<String>) -> List<String>
    )

    /**
     * Called by CommandManager on construction to allow the registrar to install
     * default/built-in transformers and platform-specific ones.
     * Default implementation registers basic types.
     */
    fun initialize(manager: CommandManager<S>) {
        manager.registerTransformer(Boolean::class.java, BooleanTransformer())
        manager.registerTransformer(String::class.java, StringTransformer())
        manager.registerTransformer(Int::class.java, IntTransformer())
        manager.registerTransformer(Float::class.java, FloatTransformer())
        manager.registerTransformer(Double::class.java, DoubleTransformer())
    }
}
