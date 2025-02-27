package mca.entity;

import mca.api.API;
import mca.core.MCA;
import mca.enums.AgeState;
import mca.enums.Gender;
import mca.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VillagerFactory {
    private final World world;
    private final VillagerEntityMCA villager;
    private boolean isNameSet;
    private boolean isProfessionSet;
    private boolean isGenderSet;
    private boolean isPositionSet;
    private boolean isAgeSet;
    private boolean isLevelSet;

    private VillagerFactory(World world) {
        this.world = world;
        this.villager = new VillagerEntityMCA(world);
    }

    public static VillagerFactory newVillager(World world) {
        return new VillagerFactory(world);
    }

    public VillagerFactory withGender(Gender gender) {
        villager.gender.set(gender.getId());
        isGenderSet = true;
        return this;
    }

    public VillagerFactory withProfession(VillagerProfession prof) {
        VillagerData data = villager.getVillagerData();
        villager.setVillagerData(new VillagerData(data.getType(), prof, 0));
        isProfessionSet = true;
        return this;
    }

    public VillagerFactory withProfession(VillagerProfession prof, int level) {
        VillagerData data = villager.getVillagerData();
        villager.setVillagerData(new VillagerData(data.getType(), prof, level));
        isProfessionSet = true;
        isLevelSet = true;
        return this;
    }

    public VillagerFactory withName(String name) {
        villager.villagerName.set(name);
        isNameSet = true;
        return this;
    }

    public VillagerFactory withPosition(double posX, double posY, double posZ) {
        isPositionSet = true;
        villager.setPos(posX, posY, posZ);
        return this;
    }

    public VillagerFactory withPosition(Entity entity) {
        isPositionSet = true;
        villager.setPos(entity.getX(), entity.getY(), entity.getZ());
        return this;
    }

    public VillagerFactory withPosition(BlockPos pos) {
        isPositionSet = true;
        villager.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        return this;
    }

    public VillagerFactory withAge(int age) {
        villager.setAge(age);
        isAgeSet = true;
        return this;
    }

    public VillagerFactory spawn() {
        if (!isPositionSet) {
            MCA.log("Attempted to spawn villager without a position being set!");
        }

        WorldUtils.spawnEntity(world, villager);
        return this;
    }

    public VillagerEntityMCA build() {
        if (!isGenderSet) {
            villager.gender.set(Gender.getRandom().getId());
        }

        if (!isNameSet) {
            villager.villagerName.set(API.getRandomName(Gender.byId(villager.gender.get())));
        }

        if (!isProfessionSet) {
            VillagerData data = villager.getVillagerData();
            villager.setVillagerData(new VillagerData(data.getType(), API.randomProfession(), data.getLevel()));
        }

        if (!isLevelSet) {
            VillagerData data = villager.getVillagerData();
            villager.setVillagerData(new VillagerData(data.getType(), data.getProfession(), 0));
        }

        if (!isAgeSet) {
            //give it a random age between baby and adult
            villager.setAge(villager.getRandom().nextInt(AgeState.startingAge * 2) - AgeState.startingAge);
        }

        return villager;
    }
}