package mca.command;

import cobalt.minecraft.util.CText;
import cobalt.minecraft.world.CWorld;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mca.core.Constants;
import mca.core.minecraft.ItemsMCA;
import mca.entity.EntityVillagerMCA;
import mca.entity.data.Memories;
import mca.entity.data.Village;
import mca.entity.data.VillageManagerData;
import mca.items.ItemBaby;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.stream.Stream;

public class CommandMCAAdmin {
    private static final ArrayList<EntityVillagerMCA> prevVillagersRemoved = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mca-admin")
                .then(register("help", CommandMCAAdmin::displayHelp))
                .then(register("clv", CommandMCAAdmin::clearLoadedVillagers))
                .then(register("rcv", CommandMCAAdmin::restoreClearedVillagers))
                .then(register("ffh", CommandMCAAdmin::forceFullHearts))
                .then(register("fbg", CommandMCAAdmin::forceBabyGrowth))
                .then(register("fcg", CommandMCAAdmin::forceChildGrowth))
                .then(register("inh", CommandMCAAdmin::incrementHearts))
                .then(register("deh", CommandMCAAdmin::decrementHearts))
                //.then(register("sgr", CommandMCAAdmin::spawnGrimReaper))
                //.then(register("kgr", CommandMCAAdmin::killGrimReaper))
                //.then(register("dpd", CommandMCAAdmin::dumpPlayerData))
                //.then(register("rvd", CommandMCAAdmin::resetVillagerData))
                //.then(register("rpd", CommandMCAAdmin::resetPlayerData))
                .then(register("listVillages", CommandMCAAdmin::listVillages))
                .then(register("removeVillage").then(Commands.argument("id", IntegerArgumentType.integer()).executes(CommandMCAAdmin::removeVillage)))
        );
    }

    private static int listVillages(CommandContext<CommandSource> ctx) {
        for (Village village : VillageManagerData.get(CWorld.fromMC(ctx.getSource().getLevel())).villages.values()) {
            success(String.format("%d: %s with %d buildings and %d/%d villager",
                    village.getId(),
                    village.getName(),
                    village.getBuildings().size(),
                    village.getPopulation(),
                    village.getMaxPopulation()
            ), ctx);
        }
        return 0;
    }

    private static int removeVillage(CommandContext<CommandSource> ctx) {
        int id = IntegerArgumentType.getInteger(ctx, "id");
        Map<Integer, Village> villages = VillageManagerData.get(CWorld.fromMC(ctx.getSource().getLevel())).villages;
        if (villages.containsKey(id)) {
            villages.remove(id);
            VillageManagerData.get(CWorld.fromMC(ctx.getSource().getLevel())).cache.clear();
            success("Village deleted.", ctx);
        } else {
            fail("Village with this ID does not exist.", ctx);
        }
        return 0;
    }

    private static int clearVillagerEditors(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.getItem() == ItemsMCA.VILLAGER_EDITOR.get()) {
                player.inventory.removeItem(stack);
            }
        }
        return 0;
    }

    private static int resetPlayerData(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int resetVillagerData(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int dumpPlayerData(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int killGrimReaper(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int spawnGrimReaper(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int decrementHearts(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        getLoadedVillagers(ctx).forEach(v -> {
            Memories memories = ((EntityVillagerMCA) v).getMemoriesForPlayer(player);
            memories.setHearts(memories.getHearts() - 10);
            ((EntityVillagerMCA) v).updateMemories(memories);
        });
        return 0;
    }

    private static int incrementHearts(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        getLoadedVillagers(ctx).forEach(v -> {
            Memories memories = ((EntityVillagerMCA) v).getMemoriesForPlayer(player);
            memories.setHearts(memories.getHearts() + 10);
            ((EntityVillagerMCA) v).updateMemories(memories);
        });
        return 0;
    }

    private static int forceChildGrowth(CommandContext<CommandSource> ctx) {
        getLoadedVillagers(ctx).forEach(v -> ((EntityVillagerMCA) v).setAge(0));
        return 0;
    }

    private static int forceBabyGrowth(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        ItemStack heldStack = player.getMainHandItem();

        if (heldStack.getItem() instanceof ItemBaby) {
            //((ItemBaby) heldStack.getItem()).forceAgeUp();
        }
        return 0;
    }

    private static int forceFullHearts(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        getLoadedVillagers(ctx).forEach(v -> {
            Memories memories = ((EntityVillagerMCA) v).getMemoriesForPlayer(player);
            memories.setHearts(100);
            ((EntityVillagerMCA) v).updateMemories(memories);
        });
        return 0;
    }

    private static int restoreClearedVillagers(CommandContext<CommandSource> ctx) {
        ServerWorld world = ctx.getSource().getLevel();
//        prevVillagersRemoved.forEach(world::addEntity);
        prevVillagersRemoved.clear();
        success("Restored cleared villagers.", ctx);
        return 0;
    }

    private static ArgumentBuilder<CommandSource, ?> register(String name, Command<CommandSource> cmd) {
        return Commands.literal(name).requires(cs -> cs.hasPermission(2)).executes(cmd);
    }

    private static ArgumentBuilder<CommandSource, ?> register(String name) {
        return Commands.literal(name).requires(cs -> cs.hasPermission(2));
    }

    private static int clearLoadedVillagers(final CommandContext<CommandSource> ctx) {
        prevVillagersRemoved.clear();
        getLoadedVillagers(ctx).forEach(v -> {
            prevVillagersRemoved.add((EntityVillagerMCA) v);
            v.remove(true);
        });

        success("Removed loaded villagers.", ctx);
        return 0;
    }

    private static Stream<Entity> getLoadedVillagers(final CommandContext<CommandSource> ctx) {
        return ctx.getSource().getLevel().getEntities().filter(e -> e instanceof EntityVillagerMCA);
    }

    private static void success(String message, CommandContext<CommandSource> ctx) {
        ctx.getSource().sendSuccess(new StringTextComponent(CText.Color.GREEN + message), true);
    }

    private static void fail(String message, CommandContext<CommandSource> ctx) {
        ctx.getSource().sendFailure(new StringTextComponent(CText.Color.RED + message));
    }

    private static int displayHelp(CommandContext<CommandSource> ctx) {
        sendMessage(ctx.getSource().getEntity(), Constants.Color.DARKRED + "--- " + Constants.Color.GOLD + "OP COMMANDS" + Constants.Color.DARKRED + " ---", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin ffh " + Constants.Color.GOLD + " - Force all hearts on all villagers.", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin fbg " + Constants.Color.GOLD + " - Force your baby to grow up.", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin fcg " + Constants.Color.GOLD + " - Force nearby children to grow.", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin clv " + Constants.Color.GOLD + " - Clear all loaded villagers. " + Constants.Color.RED + "(IRREVERSABLE)", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin rcv " + Constants.Color.GOLD + " - Restores cleared villagers. ", true);

        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin listVillages " + Constants.Color.GOLD + " - Prints a list of all villages.", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin removeVillage id" + Constants.Color.GOLD + " - Removed a village with given id.", true);

        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin inh " + Constants.Color.GOLD + " - Increase hearts by 10.", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin deh " + Constants.Color.GOLD + " - Decrease hearts by 10.", true);
        //sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin kgr " + Constants.Color.GOLD + " - Kill all Grim Reapers in the world.", true);
        //sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin dpd " + Constants.Color.GOLD + " - Dumps player data to chat.", true);
        //sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin rvd <UUID>" + Constants.Color.GOLD + " - Resets the given villager.", true);
        //sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin rpd <PlayerName>" + Constants.Color.GOLD + " - Resets the given player's MCA data.", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin cve" + Constants.Color.GOLD + " - Remove all villager editors from the game.", true);

        sendMessage(ctx.getSource().getEntity(), Constants.Color.DARKRED + "--- " + Constants.Color.GOLD + "GLOBAL COMMANDS" + Constants.Color.DARKRED + " ---", true);
        sendMessage(ctx.getSource().getEntity(), Constants.Color.WHITE + " /mca-admin help " + Constants.Color.GOLD + " - Shows this list of commands.", true);
        return 0;
    }

    private static void sendMessage(Entity commandSender, String message, boolean noPrefix) {
        if (noPrefix) {
            commandSender.sendMessage(new StringTextComponent(message), Util.NIL_UUID);
        } else {
            sendMessage(commandSender, message);
        }
    }

    private static void sendMessage(Entity commandSender, String message) {
        commandSender.sendMessage(new StringTextComponent(Constants.Color.GOLD + "[MCA] " + Constants.Format.RESET + message), Util.NIL_UUID);
    }
}