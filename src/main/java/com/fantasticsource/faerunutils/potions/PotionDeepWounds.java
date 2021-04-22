package com.fantasticsource.faerunutils.potions;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.potions.BetterPotion;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class PotionDeepWounds extends BetterPotion
{
    protected PotionDeepWounds()
    {
        super(new ResourceLocation(MODID, "deepwounds"), new ResourceLocation(MODID, "potions/deepwounds.png"), true, false, 0xaa0000);
        registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "f5209996-9699-42b8-9bf1-668abaab7d75", -1, 0);
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
    {
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.setHealth(Tools.min(entityLivingBaseIn.getHealth(), entityLivingBaseIn.getMaxHealth()));
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingHurt(LivingDamageEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityPlayerMP)
        {
            PotionEffect effect = entity.getActivePotionEffect(PotionDefinitions.POTION_EFFECT_DEEP_WOUNDS);
            int level = (int) ((effect == null ? 0 : effect.getAmplifier() + 1) + event.getAmount() * 0.5);
            if (level > 0)
            {
                //Need this delay, or else it will apply the max HP reduction before applying damage, effectively applying extra damage
                ServerTickTimer.schedule(1, () -> entity.addPotionEffect(new PotionEffect(PotionDefinitions.POTION_EFFECT_DEEP_WOUNDS, Integer.MAX_VALUE, level - 1)));
            }
        }
    }
}
