package cn.monshine.commandlibrary.cli

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class CLIEnvironmentFormatTest {
    private val esc = "\u001B["

    @Test
    fun ansiEnabled_translatesColorsAndStyles_andResets() {
        val env = CLIEnvironment(ansi = true)

        val input = "&cError &7details &lbold&r normal"
        val out = env.format(input)

        assertTrue(out.contains(esc + "91m"), "should contain red (91m)")
        assertTrue(out.contains(esc + "37m"), "should contain gray (37m)")
        assertTrue(out.contains(esc + "1m"),  "should contain bold (1m)")

        assertTrue(out.endsWith("\u001B[0m"), "should end with reset")

        assertFalse(out.contains('&'))
    }

    @Test
    fun ansiDisabled_stripsCodes_outputsPlainText() {
        val env = CLIEnvironment(ansi = false)

        val input = "&cError &7details &lbold&r normal"
        val out = env.format(input)

        assertEquals("Error details bold normal", out)

        assertFalse(out.contains("\u001B["))
        assertFalse(out.contains('&'))
    }
}

