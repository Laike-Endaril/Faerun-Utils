package com.fantasticsource.faerunutils.potions;

import com.fantasticsource.mctools.potions.BetterPotion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class PotionDefinitions
{
    public static final BetterPotion POTION_EFFECT_DEEP_WOUNDS = new PotionDeepWounds();

    public static final PotionType POTIONTYPE_DEEP_WOUNDS = new PotionType(MODID + ".deepwounds", new PotionEffect(POTION_EFFECT_DEEP_WOUNDS, Integer.MAX_VALUE)).setRegistryName(MODID, "deepwounds");

    @SubscribeEvent
    public static void registerPotionEffects(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().register(POTION_EFFECT_DEEP_WOUNDS);
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event)
    {
        event.getRegistry().register(POTIONTYPE_DEEP_WOUNDS);
    }
}
