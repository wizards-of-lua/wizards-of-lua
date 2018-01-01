package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@RunWith(MinecraftJUnitRunner.class)
public class ItemTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ItemTest test_putNbt_set_repair_Cost
  @Test
  public void test_putNbt_set_repair_Cost() throws Exception {
    // Given:
    String expected = "3";

    // TODO this is not working when we won't initialize the repairCost with 2
    // since the value of 3 is considered a Long value but the ItemStack repairCost NBT value
    // must be an Integer value. This NBT stuff really sucks!

    // When:
    mc().player().chat(
        "/lua i=Items.get('diamond_axe'); i.repairCost=2; i:putNbt({tag={RepairCost=3}});  print( i.repairCost)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemTest test_putNbt_set_lore
  @Test
  public void test_putNbt_set_lore() throws Exception {
    // Given:
    String lore = "my-lore";

    // When:
    mc().player().chat(
        "/lua i=Items.get('diamond_axe'); i:putNbt({tag={display={Lore={\"%s\"}}}}); print( i.nbt.tag.display.Lore[1])",
        lore);

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(lore);
  }

}
