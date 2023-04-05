package xyz.wkrp;

import com.google.inject.Provides;

import javax.inject.Inject;


import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.plugins.loottracker.LootReceived;
import xyz.wkrp.records.ItemDrop;
import xyz.wkrp.records.NameSignIn;
import xyz.wkrp.records.NpcKill;
import xyz.wkrp.records.XpDrop;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
        name = "Old School Snitch"
)
public class OldSchoolSnitchPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private OldSchoolSnitchClient snitchClient;

    @Inject
    private OldSchoolSnitchConfig config;
    private final Map<Skill, Integer> previousSkillExpTable = new EnumMap<>(Skill.class);

    private boolean NameSnatched = false;
    private static final Pattern WOOD_CUT_PATTERN = Pattern.compile("You get (?:some|an)[\\w ]+(?:logs?|mushrooms)\\.");
    private static final Pattern MINING_PATTERN = Pattern.compile(
            "You " +
                    "(?:manage to|just)" +
                    " (?:mined?|quarry) " +
                    "(?:some|an?) " +
                    "(?:copper|tin|clay|iron|silver|coal|gold|mithril|adamantite|runeite|amethyst|sandstone|granite|barronite shards|barronite deposit|Opal|piece of Jade|Red Topaz|Emerald|Sapphire|Ruby|Diamond)" +
                    "(?:\\.|!)");

    private Multiset<Integer> previousInventorySnapshot;
    private Integer containerChangedCount = 0;
    private Integer pendingInventoryUpdates = 0;

    private Multiset<Integer> getInventorySnapshot() {
        final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        Multiset<Integer> inventorySnapshot = HashMultiset.create();

        if (inventory != null) {
            Arrays.stream(inventory.getItems())
                    .forEach(item -> inventorySnapshot.add(item.getId(), item.getQuantity()));
        }

        return inventorySnapshot;
    }


    @Override
    protected void startUp() throws Exception {
        log.info("Old School Snitch started!");
    }

    @Subscribe
    public void onPlayerChanged(PlayerChanged playerChanged) {
        if (!NameSnatched && playerChanged.getPlayer().getId() == client.getLocalPlayer().getId()) {
            log.info("Player id match! " + client.getLocalPlayer().getName());
            snitchClient.SignIn(new NameSignIn(client.getLocalPlayer().getName(), config.apiKey()));
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        log.info("Game state changed!: " + event.getGameState());
        if(event.getGameState() == GameState.LOGGED_IN){
            previousInventorySnapshot = getInventorySnapshot();
        }
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Old School Snitch stopped!");
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {

        // check to see that the container is the equipment or inventory
        ItemContainer container = event.getItemContainer();

        if (containerChangedCount < 1 && container == client.getItemContainer(InventoryID.INVENTORY)) {
            log.info("Setting baseline snapshot");
            previousInventorySnapshot = getInventorySnapshot();
            log.info("Inventory count: " + previousInventorySnapshot.elementSet().size());

        }
        else if(containerChangedCount > 1 && container == client.getItemContainer(InventoryID.INVENTORY)){
            log.info("Inventory changed!");
            log.info("Pending requested updates: " + pendingInventoryUpdates);
            if(pendingInventoryUpdates > 0){
                Multiset<Integer> currentInventorySnapshot = getInventorySnapshot();
                final Multiset<Integer> itemsReceived = Multisets.difference(currentInventorySnapshot, previousInventorySnapshot);
                //final Multiset<Integer> itemsRemoved = Multisets.difference(previousInventorySnapshot, currentInventorySnapshot);
                previousInventorySnapshot = currentInventorySnapshot;
                if (containerChangedCount > 1) {
                    var set = itemsReceived.elementSet();
                    log.info("Items delta: " + set.size());
                    for (var itemId : set) {
                        var count = itemsReceived.count(itemId);
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ("Got " + count + " of " + itemId), null);
                        snitchClient.sendItem(new ItemDrop(itemId, count, config.apiKey()));
                    }
                }
                pendingInventoryUpdates--;
            } else if(pendingInventoryUpdates == 0){
                log.info("non-skilling inv update, no need to grab");
            } else {
                log.error("Somehow managed to get pendingInvUpdates < 0. This is a problem");
            }
        }
        containerChangedCount++;

    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        var message = event.getMessage();
        if (event.getType() != ChatMessageType.SPAM) {
            return;
        } else if (WOOD_CUT_PATTERN.matcher(message).matches()) {
            //Got some wood
            var itemId = matchWood(message);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ("Got wood " + itemId), null);
            snitchClient.sendItem(new ItemDrop(itemId, 1, config.apiKey()));

        } else if (MINING_PATTERN.matcher(event.getMessage()).matches()) {
            //Got some ore bb
        } else if (message.contains("You catch a") || message.contains("You catch some") ||
                message.equals("Your cormorant returns with its catch.")) {
            pendingInventoryUpdates++;

        }
    }

    private int matchWood(String woodMsg) {
        if (woodMsg.contains("oak")) {
            return 1521;
        } else if (woodMsg.contains("willow")) {
            return 1519;
        } else if (woodMsg.contains("teak")) {
            return 6333;
        } else if (woodMsg.contains("maple")) {
            return 1517;
        } else if (woodMsg.contains("mahogany")) {
            return 6332;
        } else if (woodMsg.contains("arctic")) {
            return 10810;
        } else if (woodMsg.contains("yew")) {
            return 1515;
        } else if (woodMsg.contains("magic")) {
            return 1513;
        } else if (woodMsg.contains("redwood")) {
            return 19669;
        } else {
            return 1511;
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (config.xpTrackingCheckbox()) {
            final Skill skill = statChanged.getSkill();
            final int xp = statChanged.getXp();

            Integer previous = previousSkillExpTable.put(skill, xp);

            //Since we get all the skills upon login/load/whenever, we dont have to worry about seeding the table.
            if (previous != null) {
                int delta = xp - previous;
                if (delta > 0) {
                    if (config.debugMessagesCheckbox()) {
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", delta + "XP acquired in " + skill.name(), null);
                    }

                    snitchClient.sendXP(new XpDrop(skill.name(), delta, xp, config.apiKey()));
                }
            }
        }
    }

    @Subscribe
    public void onNpcLootReceived(final NpcLootReceived npcLootReceived) {
        if (config.killTrackingCheckbox()) {
            final NPC npc = npcLootReceived.getNpc();
            if (config.debugMessagesCheckbox()) {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", npc.getName() + " Killed", null);
            }
            snitchClient.sendKill(new NpcKill(npc.getId(), config.apiKey()));
        }
    }

    @Subscribe
    public void onLootReceived(final LootReceived lootReceived) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", lootReceived.getName() + " LOOT RCVD", null);
    }

    @Provides
    OldSchoolSnitchConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OldSchoolSnitchConfig.class);
    }
}
