package cn.monshine.commandlibrary.test;

import cn.monshine.commandlibrary.CommandTopic;
import cn.monshine.commandlibrary.ParamType;
import cn.monshine.commandlibrary.annotation.Command;
import cn.monshine.commandlibrary.annotation.CommandTopicGetter;
import cn.monshine.commandlibrary.annotation.Param;
import org.bukkit.entity.Player;

public class TestCommandJavaImpl {

    @CommandTopicGetter(label = "test")
    public static CommandTopic testTopic() {
        return new CommandTopic("Test Commands", java.util.Arrays.asList("This is line1", "This is line2"));
    }

    @Command(names = {"test a"}, permission = "op", description = "t")
    public static void testA(Player sender) {
        sender.sendMessage("testa");
    }

    @Command(names = {"test b"}, permission = "op", description = "t")
    public static void testB(Player sender, @Param(name = "i", type = ParamType.FLAG) boolean flag) {
        sender.sendMessage("testb");
    }

    @CommandTopicGetter(label = "test c")
    public static CommandTopic testCTopic() {
        return new CommandTopic("Test Commands (C)", java.util.Arrays.asList("This is line1", "js mei ma"));
    }

    @Command(names = {"test c d e"}, permission = "op", description = "t")
    public static void testC(Player sender, @Param(name = "a") boolean flag, @Param(name = "c") boolean flag2) {
        sender.sendMessage("testc");
    }

    @Command(names = {"ban"}, permission = "op", description = "Ban someone")
    public static void ban(Player sender, @Param(name = "s", type = ParamType.FLAG) boolean silent, Player target, @Param(name = "reason", wildcard = true) String reason) {
        sender.sendMessage("Attempt to ban " + target.getName() + " with: silent=" + silent + ", reason=" + reason);
    }
}