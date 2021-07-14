package mca.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mca.core.Constants;
import mca.core.minecraft.ItemsMCA;
import mca.entity.VillagerEntityMCA;
import mca.entity.data.Memories;
import mca.entity.data.PlayerSaveData;
import mca.entity.data.Village;
import mca.entity.data.VillageManagerData;
import mca.items.BabyItem;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

public class AdminCommand {
    private static final ArrayList<VillagerEntityMCA> prevVillagersRemoved = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mca-admin")
                .then(register("help", AdminCommand::displayHelp))
                .then(register("clearLoadedVillagers", AdminCommand::clearLoadedVillagers))
                .then(register("restoreClearedVillagers", AdminCommand::restoreClearedVillagers))
                .then(register("forceFullHearts", AdminCommand::forceFullHearts))
                .then(register("forceBabyGrowth", AdminCommand::forceBabyGrowth))
                .then(register("forceChildGrowth", AdminCommand::forceChildGrowth))
                .then(register("incrementHearts", AdminCommand::incrementHearts))
                .then(register("decrementHearts", AdminCommand::decrementHearts))
                //.then(register("dumpPlayerData", AdminCommand::dumpPlayerData))
                //.then(register("resetVillagerData", AdminCommand::resetVillagerData))
                .then(register("resetPlayerData", AdminCommand::resetPlayerData))
                .then(register("listVillages", AdminCommand::listVillages))
                .then(register().then(Commands.argument("id", IntegerArgumentType.integer()).executes(AdminCommand::removeVillage)))
        );
    }

    private static int listVillages(CommandContext<CommandSource> ctx) {
        for (Village village : VillageManagerData.get(ctx.getSource().getLevel()).villages.values()) {
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
        Map<Integer, Village> villages = VillageManagerData.get(ctx.getSource().getLevel()).villages;
        if (villages.containsKey(id)) {
            villages.remove(id);
            VillageManagerData.get(ctx.getSource().getLevel()).cache.clear();
            success("Village deleted.", ctx);
        } else {
            fail(ctx);
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
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        PlayerSaveData playerData = PlayerSaveData.get(player.level, player.getUUID());
        playerData.reset();
        return 0;
    }

    private static int resetVillagerData(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int dumpPlayerData(CommandContext<CommandSource> ctx) {
        return 0;
    }

    private static int decrementHearts(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        getLoadedVillagers(ctx).forEach(v -> {
            Memories memories = ((VillagerEntityMCA) v).getMemoriesForPlayer(player);
            memories.setHearts(memories.getHearts() - 10);
            ((VillagerEntityMCA) v).updateMemories(memories);
        });
        return 0;
    }

    private static int incrementHearts(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        getLoadedVillagers(ctx).forEach(v -> {
            Memories memories = ((VillagerEntityMCA) v).getMemoriesForPlayer(player);
            memories.setHearts(memories.getHearts() + 10);
            ((VillagerEntityMCA) v).updateMemories(memories);
        });
        return 0;
    }

    private static int forceChildGrowth(CommandContext<CommandSource> ctx) {
        getLoadedVillagers(ctx).forEach(v -> ((VillagerEntityMCA) v).setAge(0));
        return 0;
    }

    private static int forceBabyGrowth(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        ItemStack heldStack = player.getMainHandItem();

        if (heldStack.getItem() instanceof BabyItem) {
            //((ItemBaby) heldStack.getItem()).forceAgeUp();
        }
        return 0;
    }

    private static int forceFullHearts(CommandContext<CommandSource> ctx) {
        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
        getLoadedVillagers(ctx).forEach(v -> {
            Memories memories = ((VillagerEntityMCA) v).getMemoriesForPlayer(player);
            memories.setHearts(100);
            ((VillagerEntityMCA) v).updateMemories(memories);
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

    private static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("removeVillage").requires(cs -> cs.hasPermission(2));
    }

    private static int clearLoadedVillagers(final CommandContext<CommandSource> ctx) {
        prevVillagersRemoved.clear();
        getLoadedVillagers(ctx).forEach(v -> {
            prevVillagersRemoved.add((VillagerEntityMCA) v);
            v.remove(true);
        });

        success("Removed loaded villagers.", ctx);
        return 0;
    }

    private static Stream<Entity> getLoadedVillagers(final CommandContext<CommandSource> ctx) {
        return ctx.getSource().getLevel().getEntities().filter(e -> e instanceof VillagerEntityMCA);
    }

    private static void success(String message, CommandContext<CommandSource> ctx) {
        ctx.getSource().sendSuccess(new StringTextComponent(Constants.Color.GREEN + message), true);
    }

    private static void fail(CommandContext<CommandSource> ctx) {
        ctx.getSource().sendFailure(new StringTextComponent(Constants.Color.RED + "Village with this ID does not exist."));
    }

    private static int displayHelp(CommandContext<CommandSource> ctx) {
        Entity player = ctx.getSource().getEntity();
        String white = Constants.Color.WHITE;
        String gold = Constants.Color.GOLD;
        sendMessage(player, Constants.Color.DARKRED + "--- " + gold + "OP COMMANDS" + Constants.Color.DARKRED + " ---");
        sendMessage(player, white + " /mca-admin forceFullHearts " + gold + " - Force all hearts on all villagers.");
        sendMessage(player, white + " /mca-admin forceBabyGrowth " + gold + " - Force your baby to grow up.");
        sendMessage(player, white + " /mca-admin forceChildGrowth " + gold + " - Force nearby children to grow.");
        sendMessage(player, white + " /mca-admin clearLoadedVillagers " + gold + " - Clear all loaded villagers. " + Constants.Color.RED + "(IRREVERSABLE)");
        sendMessage(player, white + " /mca-admin restoreClearedVillagers " + gold + " - Restores cleared villagers. ");

        sendMessage(player, white + " /mca-admin listVillages " + gold + " - Prints a list of all villages.");
        sendMessage(player, white + " /mca-admin removeVillage id" + gold + " - Removed a village with given id.");

        sendMessage(player, white + " /mca-admin incrementHearts " + gold + " - Increase hearts by 10.");
        sendMessage(player, white + " /mca-admin decrementHearts " + gold + " - Decrease hearts by 10.");

        sendMessage(player, white + " /mca-admin resetPlayerData " + gold + " - Resets hearts, marriage statuc etc.");

        sendMessage(player, white + " /mca-admin listVillages " + gold + " - List all known villages.");
        sendMessage(player, white + " /mca-admin removeVillage " + gold + " - Remove a given village.");

        sendMessage(player, Constants.Color.DARKRED + "--- " + gold + "GLOBAL COMMANDS" + Constants.Color.DARKRED + " ---");
        sendMessage(player, white + " /mca-admin help " + gold + " - Shows this list of commands.");
        return 0;
    }


    private static void sendMessage(Entity commandSender, String message) {
        commandSender.sendMessage(new StringTextComponent(Constants.Color.GOLD + "[MCA] " + Constants.Format.RESET + message), Util.NIL_UUID);
    }
}