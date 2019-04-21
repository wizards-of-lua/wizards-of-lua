package net.wizardsoflua.tests;

import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EntityTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntityTest test_nbt_is_not_nil
  @Test
  public void test_nbt_is_not_nil() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(p.nbt~=nil)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.EntityTest test_nbt_pos_is_readable
  @Test
  public void test_nbt_pos_is_readable() throws Exception {
    // Given:
    Vec3d pos = new Vec3d(4.5, 5, 2.3);
    String expected = String.format("{ %s, %s, %s }", pos.x, pos.y, pos.z);

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.x, pos.y,
        pos.z);
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(str(p.nbt.Pos))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_nbt_is_not_writable
  @Test
  public void test_nbt_is_not_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; p.nbt = {};");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage())
        .startsWith("Error during spell execution: attempt to modify read-only table index");
  }

  // /test net.wizardsoflua.tests.EntityTest test_putNbt_setting_pos
  @Test
  public void test_putNbt_setting_pos() throws Exception {
    // Given:
    Vec3d posA = new Vec3d(1, 2, 3);
    Vec3d posB = new Vec3d(5, 6, 7);
    String expected = format(posB);

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", posA.x,
        posA.y, posA.z);
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1];p:putNbt({Pos={%s, %s, %s}}); print(p.pos)",
        posB.x, posB.y, posB.z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_facing_is_readable
  @Test
  public void test_facing_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(p.facing)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    EnumFacing expectedFacing = actEntities.get(0).getHorizontalFacing();
    assertThat(act.getMessage()).isEqualTo(expectedFacing.getName());
  }

  // /test net.wizardsoflua.tests.EntityTest test_lookVec_is_readable
  @Test
  public void test_lookVec_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[45f,45f]}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; v=p.lookVec; print(string.format('%.5f',v.x)); print(string.format('%.5f',v.y)); print(string.format('%.5f',v.z))");

    // Then:
    ServerLog4jEvent actX = mc().waitFor(ServerLog4jEvent.class);
    ServerLog4jEvent actY = mc().waitFor(ServerLog4jEvent.class);
    ServerLog4jEvent actZ = mc().waitFor(ServerLog4jEvent.class);
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    String expectedX = String.format("%.5f", ((EntityPig) actEntities.get(0)).getLookVec().x);
    String expectedY = String.format("%.5f", ((EntityPig) actEntities.get(0)).getLookVec().y);
    String expectedZ = String.format("%.5f", ((EntityPig) actEntities.get(0)).getLookVec().z);
    assertThat(actX.getMessage()).isEqualTo(expectedX);
    assertThat(actY.getMessage()).isEqualTo(expectedY);
    assertThat(actZ.getMessage()).isEqualTo(expectedZ);
  }

  // /test net.wizardsoflua.tests.EntityTest test_lookVec_is_writable
  @Test
  public void test_lookVec_is_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    String expected = "true";

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[45f,45f]}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; v=Vec3(1,0,0); p.lookVec=v; print(p.lookVec==v)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_rotationYaw_is_readable
  @Test
  public void test_rotationYaw_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; print(string.format('%.5f',p.rotationYaw))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    String expectedRotationYaw =
        String.format("%.5f", ((EntityPig) actEntities.get(0)).renderYawOffset);
    assertThat(act.getMessage()).isEqualTo(expectedRotationYaw);
  }

  // /test net.wizardsoflua.tests.EntityTest test_rotationYaw_is_writable
  @Test
  public void test_rotationYaw_is_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    float expectedRotationYaw = 45f;
    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p.rotationYaw=%s; print('ok')",
        expectedRotationYaw);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    float actualRotationYaw = ((EntityPig) actEntities.get(0)).rotationYaw;
    assertThat(actualRotationYaw).isEqualTo(expectedRotationYaw);
  }

  // /test net.wizardsoflua.tests.EntityTest test_rotationPitch_is_readable
  @Test
  public void test_rotationPitch_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; print(string.format('%.5f',p.rotationPitch))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    String expectedRotationPitch =
        String.format("%.5f", ((EntityPig) actEntities.get(0)).rotationPitch);
    assertThat(act.getMessage()).isEqualTo(expectedRotationPitch);
  }

  // /test net.wizardsoflua.tests.EntityTest test_rotationPitch_is_writable
  @Test
  public void test_rotationPitch_is_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    float expectedRotationPitch = 45f;
    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p.rotationPitch=%s; print('ok')",
        expectedRotationPitch);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    float actualRotationPitch = ((EntityPig) actEntities.get(0)).rotationPitch;
    assertThat(actualRotationPitch).isEqualTo(expectedRotationPitch);
  }

  // /test net.wizardsoflua.tests.EntityTest test_eyeHeight_is_readable
  @Test
  public void test_eyeHeight_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; print(string.format('%.5f',p.eyeHeight))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    String expectedEyeHeight =
        String.format("%.5f", ((EntityPig) actEntities.get(0)).getEyeHeight());
    assertThat(act.getMessage()).isEqualTo(expectedEyeHeight);
  }

  // /test net.wizardsoflua.tests.EntityTest test_motion_is_readable
  @Test
  public void test_motion_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig}", pos.getX(),
        pos.getY() + 10, pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; m=p.motion; print(m.y<0)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.EntityTest test_motion_is_writable
  @Test
  public void test_motion_is_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p.motion=Vec3(0,10,0); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    double actualMotion = ((EntityPig) actEntities.get(0)).motionY;
    assertThat(actualMotion).isGreaterThan(0);
  }

  // /test net.wizardsoflua.tests.EntityTest test_tags_is_readable
  @Test
  public void test_tags_is_readable() throws Exception {
    // Given:
    String tag = "demotag";
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,Tags:[\"%s\"]}",
        pos.getX(), pos.getY(), pos.getZ(), tag);
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(str(p.tags))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{ \"" + tag + "\" }");
  }

  // /test net.wizardsoflua.tests.EntityTest test_tags_is_writable
  @Test
  public void test_tags_is_writable() throws Exception {
    // Given:
    String initialTag = "initialtag";
    String newTag1 = "newtag1";
    String newTag2 = "newtag2";
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,Tags:[\"%s\"]}",
        pos.getX(), pos.getY(), pos.getZ(), initialTag);
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p.tags={'%s','%s'}; print('ok')", newTag1,
        newTag2);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    Set<String> actualTags = ((EntityPig) actEntities.get(0)).getTags();
    assertThat(actualTags).containsOnly(newTag1, newTag2);
  }

  // /test net.wizardsoflua.tests.EntityTest test_addTag
  @Test
  public void test_addTag() throws Exception {
    // Given:
    String initialTag = "initialtag";
    String newTag = "newtag";

    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,Tags:[\"%s\"]}",
        pos.getX(), pos.getY(), pos.getZ(), initialTag);
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; p:addTag('%s'); print('ok')",
        newTag);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    Set<String> actualTags = ((EntityPig) actEntities.get(0)).getTags();
    assertThat(actualTags).containsOnly(initialTag, newTag);
  }

  // /test net.wizardsoflua.tests.EntityTest test_removeTag
  @Test
  public void test_removeTag() throws Exception {
    // Given:
    String initialTag = "initialtag";

    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,Tags:[\"%s\"]}",
        pos.getX(), pos.getY(), pos.getZ(), initialTag);
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p:removeTag('%s'); print('ok')", initialTag);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    Set<String> actualTags = ((EntityPig) actEntities.get(0)).getTags();
    assertThat(actualTags).isEmpty();
  }

  // /test net.wizardsoflua.tests.EntityTest test_move_forward
  @Test
  public void test_move_forward() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint().up();
    float rotation = 90;
    BlockPos expectedPos = pos.west();

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[%sf,0f]}", pos.getX(),
        pos.getY(), pos.getZ(), rotation);
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p:move('forward'); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    BlockPos actPos = ((EntityPig) actEntities.get(0)).getPosition();
    assertThat(actPos).isEqualTo(expectedPos);
  }

  // /test net.wizardsoflua.tests.EntityTest test_move_back
  @Test
  public void test_move_back() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint().up();
    float rotation = 90;
    BlockPos expectedPos = pos.east();

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[%sf,0f]}", pos.getX(),
        pos.getY(), pos.getZ(), rotation);
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; p:move('back'); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    BlockPos actPos = ((EntityPig) actEntities.get(0)).getPosition();
    assertThat(actPos).isEqualTo(expectedPos);
  }

  // /test net.wizardsoflua.tests.EntityTest test_move_left
  @Test
  public void test_move_left() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint().up();
    float rotation = 90;
    BlockPos expectedPos = pos.south();

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[%sf,0f]}", pos.getX(),
        pos.getY(), pos.getZ(), rotation);
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; p:move('left'); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    BlockPos actPos = ((EntityPig) actEntities.get(0)).getPosition();
    assertThat(actPos).isEqualTo(expectedPos);
  }

  // /test net.wizardsoflua.tests.EntityTest test_move_right
  @Test
  public void test_move_right() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint().up();
    float rotation = 90;
    BlockPos expectedPos = pos.north();

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[%sf,0f]}", pos.getX(),
        pos.getY(), pos.getZ(), rotation);
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p:move('right'); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    BlockPos actPos = ((EntityPig) actEntities.get(0)).getPosition();
    assertThat(actPos).isEqualTo(expectedPos);
  }

  // /test net.wizardsoflua.tests.EntityTest test_scanView
  @Test
  public void test_scanView() throws Exception {
    // Given:
    BlockPos pigpos = mc().getWorldSpawnPoint();
    float rotation = 0; // facing south
    BlockPos targetPos = pigpos.south(5);
    mc().setBlock(targetPos, Blocks.ANVIL);
    String expected = "anvil";

    mc().executeCommand(
        "/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1,Rotation:[%sf,0f]}",
        pigpos.getX(), pigpos.getY(), pigpos.getZ(), rotation);
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; v=Vec3(0,0,1); p.lookVec=v; h=p:scanView(10); spell.pos=h.pos; print(spell.block.name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_dropItem
  @Test
  public void test_dropItem() throws Exception {
    // Given:
    BlockPos pigpos = mc().getWorldSpawnPoint();
    BlockPos targetPos = pigpos.south(5);
    mc().setBlock(targetPos, Blocks.ANVIL);
    String expected = "wheat,5";

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pigpos.getX(),
        pigpos.getY(), pigpos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; i=Items.get('wheat',5); p:dropItem(i); drop=Entities.find('@e[name=item.item.wheat]')[1]; print(drop.item.id..','..drop.item.count)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_dropItem_result
  @Test
  public void test_dropItem_result() throws Exception {
    // Given:
    BlockPos pigpos = mc().getWorldSpawnPoint();
    BlockPos targetPos = pigpos.south(5);
    mc().setBlock(targetPos, Blocks.ANVIL);
    String expected = "wheat,5";

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pigpos.getX(),
        pigpos.getY(), pigpos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; drop=p:dropItem(Items.get('wheat',5)); print(drop.item.id..','..drop.item.count)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_alive_is_readable
  @Test
  public void test_alive_is_readable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p:kill(); sleep(1); print(p.alive)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.EntityTest test_kill
  @Test
  public void test_kill() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[name=testpig]')[1]; p:kill(); sleep(1); print('ok')");
    mc().clearEvents();

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    List<? extends Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(0);
  }

  // /test net.wizardsoflua.tests.EntityTest test_entityType_is_pig
  @Test
  public void test_entityType_is_pig() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());

    // When:
    mc().player().chat("/lua p=Entities.find('@e[name=testpig]')[1] print(p.entityType)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("pig");
  }

  // /test net.wizardsoflua.tests.EntityTest test_world_is_readable
  @Test
  public void test_world_is_readable() throws Exception {
    // Given
    BlockPos pos = mc().getWorldSpawnPoint();
    String expected = mc().getWorldName();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; w=p.world; print(w.name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }
}
