package com.fantasticsource.faerunutils;

import com.fantasticsource.faeruncharacters.PatreonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class ServerChatAlterer
{
    public static final HashMap<Integer, String> PATREON_NAMES = new HashMap<>();

    @SubscribeEvent
    public static void serverChat(ServerChatEvent event)
    {
        EntityPlayerMP player = event.getPlayer();
        int cents = PatreonHandler.getPlayerPatreonCents(player);

        int max = -1;
        String newName = "@p";

        for (Map.Entry<Integer, String> entry : PATREON_NAMES.entrySet())
        {
            int req = entry.getKey();
            if (cents >= req && req > max)
            {
                max = req;
                newName = entry.getValue();
            }
        }

        ITextComponent comp = new TextComponentTranslation("chat.type.text", newName.replaceAll("@p", player.getDisplayName().getFormattedText()), ForgeHooks.newChatWithLinks(event.getMessage()));
        event.setComponent(comp);
    }
}
