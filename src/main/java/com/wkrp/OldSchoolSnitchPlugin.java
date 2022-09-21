package com.wkrp;

import com.google.inject.Provides;

import javax.inject.Inject;

import com.wkrp.records.NpcKill;
import com.wkrp.records.XpDrop;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

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


    @Override
    protected void startUp() throws Exception {
        log.info("Old School Snitch started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Old School Snitch stopped!");
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
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", delta + "XP acquired in " + skill.name(), null);
                    snitchClient.sendXP(new XpDrop(skill.name(), delta, UUID.fromString(config.apiKey())));
                }
            }
        }

    }

    @Subscribe
    public void onNpcLootReceived(final NpcLootReceived npcLootReceived) {
        if (config.killrackingCheckbox()) {
            final NPC npc = npcLootReceived.getNpc();

            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", npc.getName() + "Killed", null);
            snitchClient.sendKill(new NpcKill(npc.getId(), UUID.fromString(config.apiKey())));
        }

    }

    @Provides
    OldSchoolSnitchConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OldSchoolSnitchConfig.class);
    }
}
