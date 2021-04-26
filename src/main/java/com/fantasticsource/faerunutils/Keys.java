package com.fantasticsource.faerunutils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding
            MAINHAND = new KeyBinding(MODID + ".key.mainhand", KeyConflictContext.IN_GAME, -100, MODID + ".keyCategory"),
            MAINHAND_2 = new KeyBinding(MODID + ".key.mainhand2", KeyConflictContext.IN_GAME, KeyModifier.ALT, -100, MODID + ".keyCategory"),
            OFFHAND = new KeyBinding(MODID + ".key.offhand", KeyConflictContext.IN_GAME, -99, MODID + ".keyCategory"),
            OFFHAND_2 = new KeyBinding(MODID + ".key.offhand2", KeyConflictContext.IN_GAME, KeyModifier.ALT, -99, MODID + ".keyCategory"),
            KICK = new KeyBinding(MODID + ".key.kick", KeyConflictContext.IN_GAME, Keyboard.KEY_E, MODID + ".keyCategory");


    public static void init(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(Keys.class);

        for (KeyBinding keyBinding : new KeyBinding[]{MAINHAND, MAINHAND_2, OFFHAND, OFFHAND_2, KICK}) ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent event)
    {
        if (MAINHAND.isKeyDown() && MAINHAND.isPressed())
        {
            Network.WRAPPER.sendToServer(new Network.ControlPacket("mainhand"));
        }
        else if (MAINHAND_2.isKeyDown() && MAINHAND_2.isPressed())
        {
            Network.WRAPPER.sendToServer(new Network.ControlPacket("mainhand2"));
        }
        else if (OFFHAND.isKeyDown() && OFFHAND.isPressed())
        {
            Network.WRAPPER.sendToServer(new Network.ControlPacket("offhand"));
        }
        else if (OFFHAND_2.isKeyDown() && OFFHAND_2.isPressed())
        {
            Network.WRAPPER.sendToServer(new Network.ControlPacket("offhand2"));
        }
        else if (KICK.isKeyDown() && KICK.isPressed())
        {
            Network.WRAPPER.sendToServer(new Network.ControlPacket("kick"));
        }
    }
}
