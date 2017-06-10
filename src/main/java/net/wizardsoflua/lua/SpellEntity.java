package net.wizardsoflua.lua;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.wizardsoflua.WizardsOfLua;

public class SpellEntity extends Entity {
  public static final String NAME = "Spell";
  public static final int ID = 1;
  private static final ResourceLocation RES_LOCATION =
      new ResourceLocation(WizardsOfLua.MODID + ":" + SpellEntity.NAME);

  public static void register() {
    Object mod = WizardsOfLua.instance;
    int trackingRange = 0;
    int updateFrequency = 1;
    boolean sendsVelocityUpdates = false;
    EntityRegistry.registerModEntity(RES_LOCATION, SpellEntity.class, NAME, ID, mod, trackingRange,
        updateFrequency, sendsVelocityUpdates);
  }

  public SpellEntity(World worldIn) {
    super(worldIn);
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound compound) {}

  @Override
  protected void writeEntityToNBT(NBTTagCompound compound) {}

  @Override
  protected void entityInit() {}

  @Override
  public void onUpdate() {
    super.onUpdate();
  }


}
