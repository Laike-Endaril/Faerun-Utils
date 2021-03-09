package com.fantasticsource.faerunutils;

import com.fantasticsource.faeruncharacters.PatreonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
        //Patreon name formatting
        EntityPlayerMP player = event.getPlayer();
        int cents = PatreonHandler.getPlayerPatreonCents(player);

        int max = -1;
        String name = "@p";

        for (Map.Entry<Integer, String> entry : PATREON_NAMES.entrySet())
        {
            int req = entry.getKey();
            if (cents >= req && req > max)
            {
                max = req;
                name = entry.getValue();
            }
        }

        name = name.replaceAll("@p", player.getDisplayName().getFormattedText());


        //Gold player messages to differentiate from NPC chatter, etc
        ITextComponent message = ForgeHooks.newChatWithLinks(event.getMessage());
        message.getStyle().setColor(TextFormatting.GOLD);


        //Itemstack chat links
//        for (int i = 0; i < message.)
//            for (ITextComponent c : message.getSiblings()) System.out.println(c.getFormattedText());
//        player.getHeldItemMainhand().getTextComponent();


        //Save changes
        event.setComponent(new TextComponentTranslation("chat.type.text", name, message));
    }
}
