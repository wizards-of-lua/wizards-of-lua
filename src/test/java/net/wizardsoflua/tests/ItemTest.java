package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class ItemTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ItemTest test_putNbt_set_repair_Cost
  @Test
  public void test_putNbt_set_repair_Cost() throws Exception {
    // Given:
    String expected = "3";

    // When:
    mc().player().chat(
        "/lua i=Items.get('diamond_axe'); i:putNbt({tag={RepairCost=3}});  print( i.repairCost)");

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
