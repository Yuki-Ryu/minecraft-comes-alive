package mca.cobalt.minecraft.network.datasync;

import mca.cobalt.minecraft.nbt.CNBT;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class CStringParameter extends CDataParameter<String> {
    private final EntityDataManager data;
    private final String defaultValue;

    public CStringParameter(String id, Class<? extends Entity> e, EntityDataManager d, String dv) {
        super(id, e, DataSerializers.STRING);
        data = d;
        defaultValue = dv;
    }

    public String get() {
        return data.get(param);
    }

    public void set(String v) {
        data.set(param, v);
    }

    @Override
    public void register() {
        data.define(param, defaultValue);
    }

    @Override
    public void load(CNBT nbt) {
        set(nbt.getString(id));
    }

    @Override
    public void save(CNBT nbt) {
        nbt.setString(id, get());
    }
}
