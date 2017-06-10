package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.lua.SpellProgram;

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

  private ICommandSender owner;
  private String text;
  private SpellProgram program;

  public SpellEntity(World world) {
    super(checkNotNull(world, "world==null!"));
  }

  public SpellEntity(World world, ICommandSender owner, String text) {
    this(world);
    this.owner = checkNotNull(owner, "owner==null!");
    this.text = checkNotNull(text, "text==null!");
    this.program = new SpellProgram(owner, text);
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
    if (program == null) {
      setDead();
      return;
    }
    try {
      program.resume();
    } catch (SpellException e) {
      e.printStackTrace();
      String message = String.format("Error during command execution: %s", e.getMessage());
      TextComponentString txt = new TextComponentString(message);
      txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
      owner.sendMessage(txt);
    }
    if (program.isTerminated()) {
      setDead();
    }
  }
}
