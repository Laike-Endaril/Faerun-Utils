package com.fantasticsource.faerunutils;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DamageBlocker
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingHurt(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();
        if (source == DamageSource.IN_FIRE || source == DamageSource.CACTUS)
        {
            event.setCanceled(true);
        }
    }
}
