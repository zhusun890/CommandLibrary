package cn.monshine.commandlibrary

import org.bukkit.ChatColor

/**
 * Translates a string into Minecraft's color code syntax.
 *
 * @return The translated string.
 */
fun String.colorize(): String = ChatColor.translateAlternateColorCodes('&', this)

/**
 * Converts the array to a list, ignoring empty strings.
 *
 * @return A list of non-empty strings from the original array.
 */
fun Array<out String>.toListIgnoreEmptyString(): MutableList<String> {
    val list = mutableListOf<String>()

    for (element in this) {
        if (element.isEmpty()) continue
        if (element.toCharArray().isEmpty()) continue
        list.add(element)
    }

    return list
}