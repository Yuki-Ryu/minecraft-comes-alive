package mca.core.forge;

import mca.core.minecraft.BlocksMCA;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;

import java.util.Set;

public class PointOfInterestTypeMCA {
    public static final RegistryObject<PointOfInterestType> JEWELER = register("jeweler", () -> getBlockStates(BlocksMCA.JEWELER_WORKBENCH.get()), 1, 1);

    public static void register() {
    }

    private static Set<BlockState> getBlockStates(Block jWorkbench) {
        return (Set<BlockState>) getBlockStates(jWorkbench) ;
    }

    private static <T extends PointOfInterestType> RegistryObject<T> register(String name, Set<BlockState> blockStates, int i, int i1) {
        RegistryObject<PointOfInterestType> ret = register( name,  blockStates,  i,  i1);
        Registration.POI_TYPES.register(name, () -> new BlockItem(ret.get(), new BlockState(name, blockStates, i, i1));
        return ret;
    }

}
