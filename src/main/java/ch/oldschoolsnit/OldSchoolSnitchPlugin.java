package ch.oldschoolsnit;

import ch.oldschoolsnit.models.GLTF;
import ch.oldschoolsnit.models.ModelSnapshot;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.events.ServerNpcLoot;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.ItemStack;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.loottracker.LootRecordType;

import com.google.inject.Provides;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import ch.oldschoolsnit.records.*;

@Slf4j
@PluginDescriptor(
	name = "Old School Snitch"
)
public class OldSchoolSnitchPlugin extends Plugin
{
	private static OldSchoolSnitchPlugin instance;

	public static Client getClient()
	{
		return instance.client;
	}

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	ScheduledExecutorService executor;

	@Inject
	private EventBus eventBus;

	@Inject
	private OldSchoolSnitchClient snitchClient;

	@Inject
	private OldSchoolSnitchConfig config;

	@Inject
	private ClientToolbar clientToolbar;
	private OldSchoolSnitchPanel oldSchoolSnitchPanel;
	private NavigationButton navButton;
	private final Map<Skill, Integer> previousSkillExpTable = new EnumMap<>(Skill.class);
	private static final Pattern WOOD_CUT_PATTERN = Pattern.compile("You get (?:some|an)[\\w ]+(?:logs?|mushrooms)\\.");
	private static final Pattern MINING_PATTERN = Pattern.compile(
		"You " +
			"(?:manage to|just)" +
			" (?:mined?|quarry) " +
			"(?:some|an?) " +
			"(?:copper|tin|clay|iron|silver|coal|gold|mithril|adamantite|runite|amethyst|sandstone|granite|barronite shards|barronite deposit|Opal|piece of Jade|Red Topaz|Emerald|Sapphire|Ruby|Diamond)" +
			"(?:\\.|!)");

	private Multiset<Integer> previousInventorySnapshot;
	private Integer containerChangedCount = 0;
	private Integer pendingInventoryUpdates = 0;
	private WorldPoint currentLocation = null;
	private String playerName = "";
	private BlastMineTrackingHelper blastMineTrackingHelper = new BlastMineTrackingHelper();

	private Multiset<Integer> getInventorySnapshot()
	{
		final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		Multiset<Integer> inventorySnapshot = HashMultiset.create();

		if (inventory != null)
		{
			Arrays.stream(inventory.getItems())
				.forEach(item -> inventorySnapshot.add(item.getId(), item.getQuantity()));
		}

		return inventorySnapshot;
	}


	@Override
	protected void startUp() throws Exception
	{
		log.debug("Old School Snitch started!");

		oldSchoolSnitchPanel = injector.getInstance(OldSchoolSnitchPanel.class);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Old School Snitch")
			.icon(icon)
			.priority(3)
			.panel(oldSchoolSnitchPanel)
			.build();

		clientToolbar.addNavigation(navButton);

		OldSchoolSnitchPlugin.instance = this;
	}

	@Subscribe
	public void onPlayerChanged(PlayerChanged playerChanged)
	{
		if (playerChanged.getPlayer().getId() == client.getLocalPlayer().getId() && !config.apiKey().isBlank())
		{
			var name = client.getLocalPlayer().getName();
			if(!Objects.equals(name, playerName)){
				var apiKey = config.apiKey();
				var accountHash = client.getAccountHash();
				var accountType = client.getVarbitValue(Varbits.ACCOUNT_TYPE);
				snitchClient.SignIn(new NameSignIn(name, apiKey, accountHash, accountType));
				playerName = name;
			}
			else{
				log.debug("Skipping name update");
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			previousInventorySnapshot = getInventorySnapshot();
		}

		else if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			previousSkillExpTable.clear();
			pendingInventoryUpdates = 0;
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Old School Snitch stopped!");
		clientToolbar.removeNavigation(navButton);
	}

	private boolean doLocation(){
		return !isTempWorld() && !config.apiKey().isBlank() && config.locationTrackingCheckbox();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (doLocation())
		{
			Long accountHash = this.client.getAccountHash();
			String apiKey = config.apiKey();
			var loc = client.getLocalPlayer().getWorldLocation();
			int x = loc.getX();
			int y = loc.getY();
			int plane = loc.getPlane();

			if (loc != null && currentLocation != null && (x != currentLocation.getX() || y != currentLocation.getY() || plane != currentLocation.getPlane()))
			{
				snitchClient.sendLocation(new UserLocation(x, y, apiKey, accountHash));
			}
			currentLocation = loc;
		}
	}

	private boolean isTempWorld()
	{
		var worldType = client.getWorldType();
		return worldType.contains(WorldType.BETA_WORLD)
			|| worldType.contains(WorldType.DEADMAN)
			|| worldType.contains(WorldType.FRESH_START_WORLD)
			|| worldType.contains(WorldType.LAST_MAN_STANDING)
			|| worldType.contains(WorldType.NOSAVE_MODE)
			|| worldType.contains(WorldType.PVP_ARENA)
			|| worldType.contains(WorldType.QUEST_SPEEDRUNNING)
			|| worldType.contains(WorldType.SEASONAL)
			|| worldType.contains(WorldType.TOURNAMENT_WORLD);
	}

	private boolean doLoot(){
		return !isTempWorld() && !config.apiKey().isBlank() && config.killAndDropTrackingCheckbox();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (!doLoot())
		{
			return;
		}
		Long accountHash = this.client.getAccountHash();
		String apiKey = config.apiKey();
		// check to see that the container is the equipment or inventory
		ItemContainer container = event.getItemContainer();

		if (containerChangedCount < 1 && container == client.getItemContainer(InventoryID.INVENTORY))
		{
			log.debug("Setting baseline snapshot");
			previousInventorySnapshot = getInventorySnapshot();
			log.debug("Inventory count: " + previousInventorySnapshot.elementSet().size());

		}
		else if (containerChangedCount > 1 && container == client.getItemContainer(InventoryID.INVENTORY))
		{
			log.debug("Inventory changed!");
			log.debug("Pending requested updates: " + pendingInventoryUpdates);
			if (pendingInventoryUpdates > 0)
			{
				Multiset<Integer> currentInventorySnapshot = getInventorySnapshot();
				final Multiset<Integer> itemsReceived = Multisets.difference(currentInventorySnapshot, previousInventorySnapshot);
				//final Multiset<Integer> itemsRemoved = Multisets.difference(previousInventorySnapshot, currentInventorySnapshot);
				previousInventorySnapshot = currentInventorySnapshot;
				if (containerChangedCount > 1)
				{
					var set = itemsReceived.elementSet();
					log.debug("Items delta: " + set.size());
					for (var itemId : set)
					{
						var count = itemsReceived.count(itemId);
						if (config.debugMessagesCheckbox())
						{
							client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ("Got " + count + " of " + itemId), null);
						}
						snitchClient.sendItem(new ItemDrop(itemId, count, apiKey, accountHash));
					}
				}
				pendingInventoryUpdates--;
			}
			else if (pendingInventoryUpdates == 0)
			{
				//Still need to update the inventory snapshot on say dropping items, getting non-skilling ones, etc.
				previousInventorySnapshot = getInventorySnapshot();
			}
			else
			{
				log.error("Somehow managed to get pendingInvUpdates < 0. This is a problem");
			}
		}
		containerChangedCount++;

	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (isTempWorld())
		{
			return;
		}
		var message = event.getMessage();
		if (event.getType() != ChatMessageType.SPAM)
		{
			return;
		}
		else if (WOOD_CUT_PATTERN.matcher(message).matches())
		{
			pendingInventoryUpdates++;
			log.debug("Woodcutting increasing pending inv");
		}
		else if (MINING_PATTERN.matcher(event.getMessage()).matches())
		{
			pendingInventoryUpdates++;
			log.debug("Mining increasing pending inv");
		}
		else if (message.contains("You catch a") || message.contains("You catch some") ||
			message.equals("Your cormorant returns with its catch."))
		{
			pendingInventoryUpdates++;
			log.debug("Fishing increasing pending inv");
		}
	}

	private boolean doXp()
	{
		return !isTempWorld() && !config.apiKey().isBlank();
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (doXp())
		{
			final Skill skill = statChanged.getSkill();
			final int xp = statChanged.getXp();
			Long accountHash = this.client.getAccountHash();
			String apiKey = config.apiKey();

			Integer previous = previousSkillExpTable.put(skill, xp);
			//Since we get all the skills upon login/load/whenever, we dont have to worry about seeding the table.
			if (previous != null)
			{
				int delta = xp - previous;
				if (delta > 0)
				{
					if (config.debugMessagesCheckbox())
					{
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", delta + "XP acquired in " + skill.name(), null);
					}

					snitchClient.sendXP(new XpDrop(skill.name(), delta, xp, apiKey, accountHash));

					if (statChanged.getSkill() == Skill.RUNECRAFT)
					{
						pendingInventoryUpdates++;
						log.debug("Runecrafting increasing pending inv");
					}
				}
			}
			else
			{
				snitchClient.sendXP(new XpDrop(skill.name(), 0, xp, apiKey, accountHash));
			}
		}
	}

	@Subscribe
	public void onServerNpcLoot(final ServerNpcLoot serverNpcLoot)
	{
		if (doLoot())
		{
			Long accountHash = this.client.getAccountHash();
			String apiKey = config.apiKey();
			final NPCComposition npc = serverNpcLoot.getComposition();
			if (config.debugMessagesCheckbox())
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", npc.getName() + " Killed", null);
			}
			snitchClient.sendKill(new NpcKill(npc.getId(), apiKey, accountHash));
			for (ItemStack item : serverNpcLoot.getItems())
			{
				if (config.debugMessagesCheckbox())
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", item.getQuantity() + " of item " + item.getId(), null);
				}
				snitchClient.sendItem(new ItemDrop(item.getId(), item.getQuantity(), npc.getId(), apiKey, accountHash));
			}
		}
	}

	@Subscribe
	public void onLootReceived(final LootReceived lootReceived)
	{
		if (doLoot())
		{
			Long accountHash = this.client.getAccountHash();
			String apiKey = config.apiKey();
			if (lootReceived.getType() != LootRecordType.NPC)
			{
				log.debug("Adding loot from non-npc Loot Received");
				for (ItemStack item : lootReceived.getItems())
				{
					if (config.debugMessagesCheckbox())
					{
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", item.getQuantity() + " of item " + item.getId(), null);
					}
					snitchClient.sendItem(new ItemDrop(item.getId(), item.getQuantity(), apiKey, accountHash));
				}
			}
		}
	}

	@Subscribe
	public void onModelSnapshot(final ModelSnapshot modelSnapshot)
	{
		clientThread.invokeLater(() -> {
			var model = client.getLocalPlayer().getModel();
			String apiKey = config.apiKey();
			var gltf = new GLTF(model);
			snitchClient.sendModel(gltf, client.getAccountHash(), apiKey);
		});
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		var result = this.blastMineTrackingHelper.varbitChangedHandler(varbitChanged);
		if (result.ItemQuantity > 0)
		{
			log.debug("Item Id: {}, Item Quantity: {}", result.ItemId, result.ItemQuantity);
			Long accountHash = this.client.getAccountHash();
			String apiKey = config.apiKey();
			snitchClient.sendItem(new ItemDrop(result.ItemId, result.ItemQuantity, apiKey, accountHash));
		}
	}


	@Provides
	OldSchoolSnitchConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OldSchoolSnitchConfig.class);
	}
}
