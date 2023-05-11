
package net.unethicalite.plugins.zulrah.tasks.Banking;

import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.plugins.zulrah.data.Constants;
import net.unethicalite.plugins.zulrah.framework.SessionUpdater;

public class GetNewRingOfDueling extends SessionUpdater implements Task {
    private final WorldPoint BANK_CHEST_LOCATION = new WorldPoint(3097, 3495, 0);
    private final String BANK_CHEST_NAME = "Bank chest";

    @Override
    public boolean validate() {
        NPC banker = NPCs.getNearest(c -> c.hasAction("Bank"));
        boolean seesBanker = banker != null && banker.getWorldLocation().distanceTo(getLocalPlayer().getWorldLocation()) < 15;
        boolean noRingOfDueling = !Equipment.contains(c -> c.getName().contains(Constants.RING_OF_DUELING));

        return seesBanker && noRingOfDueling;
    }

    @Override
    public int execute() {
        getSession().setCurrentTask("Getting new ring of dueling");

        if (!Bank.isOpen()) {
            if (!BANK_CHEST_LOCATION.isInScene(getClient())) {
                getWalking().webWalk(BANK_CHEST_LOCATION);
                return 1200;
            }

            Bank.open();
            Time.sleepUntil(() -> Bank.isOpen(), 3600);
        }

        if (!Inventory.contains(c -> c.getName().startsWith(Constants.RING_OF_DUELING))) {
            Item ringOfDueling = Bank.getFirst(c -> c.getName().startsWith(Constants.RING_OF_DUELING) && c.getQuantity() > 0);
            if (ringOfDueling == null) {
                return 600;
            }

            Bank.withdraw(ringOfDueling.getName(), 1);
        }

        if (!Equipment.contains(c -> c.getName().contains(Constants.RING_OF_DUELING)) && Inventory.contains(c -> c.getName().startsWith(Constants.RING_OF_DUELING))) {
            Inventory.getFirst(c -> c.getName().startsWith(Constants.RING_OF_DUELING)).interact("Wear");
        }

        Bank.close();
        return 600;
    }
}
