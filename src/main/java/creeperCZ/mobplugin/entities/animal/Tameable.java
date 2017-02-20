package creeperCZ.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import com.sun.istack.internal.Nullable;

/**
 * Created by CreeperFace on 19.2.2017.
 */
public abstract class Tameable extends WalkingAnimal {

    protected String owner = null;
    protected String ownerUUID = null;

    public Player playerOwner = null;

    protected boolean isFollowingOwner = false;

    public Tameable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Owner")) {
            this.owner = namedTag.getString("Owner");
        }

        if (namedTag.contains("OwnerUUID")) {
            this.ownerUUID = namedTag.getString("OwnerUUID");
        }

        if (hasOwner()) {
            this.setTamed(null);
        }
    }

    public boolean hasOwner() {
        return this.owner != null;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (hasOwner()) {
            this.namedTag.putString("Owner", this.owner);
        }
    }

    public abstract boolean isTameItem(Item item);

    @Override
    public boolean onInteract(Entity entity, Item item) {
        if (isTameItem(item) && this.level.rand.nextInt(3) == 0) {

            return true;
        }

        return super.onInteract(entity, item);
    }

    public Player getOwner() {
        if (this.playerOwner == null || !this.playerOwner.isOnline()) {
            this.playerOwner = null;

            String name = this.owner.toLowerCase();

            for (Player p : getLevel().getPlayers().values()) {
                if (p.getName().toLowerCase().equals(name)) {
                    this.playerOwner = p;
                }
            }
        }

        return this.playerOwner;
    }

    public boolean isTamed() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_TAMED);
    }

    public void setTamed(@Nullable Player owner) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_TAMED);

        if (owner != null) {
            this.owner = owner.getName();
        }
    }
}
