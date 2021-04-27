package mca.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mca.core.MCA;
import net.minecraft.item.*;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum EnumChore {
    NONE(0, "none", null),
    PROSPECT(1, "gui.label.prospecting", PickaxeItem.class),
    HARVEST(2, "gui.label.harvesting", HoeItem.class),
    CHOP(3, "gui.label.chopping", AxeItem.class),
    HUNT(4, "gui.label.hunting", SwordItem.class),
    FISH(5, "gui.label.fishing", FishingRodItem.class);

    @Getter
    int id;
    String friendlyName;
    @Getter
    Class toolType;

    public static EnumChore byId(int id) {
        Optional<EnumChore> state = Arrays.stream(values()).filter((e) -> e.id == id).findFirst();
        return state.orElse(NONE);
    }

    public String getFriendlyName() {
        return MCA.localize(this.friendlyName);
    }
}

