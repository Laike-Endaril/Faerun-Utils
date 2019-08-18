package com.fantasticsource.faerunutils;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FireDamageAlterer
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingHurt(LivingAttackEvent event)
    {
        if (event.getSource() == DamageSource.IN_FIRE)
        {
            event.setCanceled(true);
        }
    }
}
