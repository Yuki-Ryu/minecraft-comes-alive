package mca.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mca.core.minecraft.ProfessionsMCA;
import mca.entity.VillagerEntityMCA;
import mca.entity.data.Village;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

@AllArgsConstructor
@Getter
public enum Constraint {
    FAMILY("family", (villager, player) -> villager.getFamilyTree().isRelative(villager.getUUID(), player.getUUID()) || villager.isMarriedTo(player.getUUID())),
    ADULTS("adult", (villager, player) -> !villager.isBaby()),
    SPOUSE("spouse", (villager, player) -> villager.isMarriedTo(player.getUUID())),
    KIDS("kids", (villager, player) -> villager.getFamilyTree().isParent(villager.getUUID(), player.getUUID())),

    CLERIC("cleric", (villager, player) -> villager.getProfession() == VillagerProfession.CLERIC),
    OUTLAWED("outlawed", (villager, player) -> villager.getProfession() == ProfessionsMCA.OUTLAWED),

    PEASANT("peasant", (villager, player) -> {
        Village village = villager.getVillage();
        return village != null && village.getRank(player).reputation >= Rank.PEASANT.reputation;
    });

    String id;
    //* Returns true if it should not show the button
    BiPredicate<VillagerEntityMCA, PlayerEntity> check;

    public static Map<Constraint, Boolean> fromStringList(String constraints) {
        Map<Constraint, Boolean> map = new HashMap<>();

        if (constraints != null && !constraints.isEmpty()) {
            String[] splitConstraints = constraints.split(",");

            for (String s : splitConstraints) {
                boolean invert = s.charAt(0) == '!';
                if (invert) {
                    s = s.substring(1);
                }
                Constraint constraint = byValue(s);
                if (constraint != null) {
                    map.put(constraint, invert);
                }
            }
        }

        return map;
    }

    public static Constraint byValue(String value) {
        Optional<Constraint> state = Arrays.stream(values()).filter((e) -> e.id.equals(value)).findFirst();
        return state.orElse(null);
    }
}

