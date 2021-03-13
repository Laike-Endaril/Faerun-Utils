package com.fantasticsource.faerunutils.potions;

import com.fantasticsource.mctools.potions.BetterPotion;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.util.ResourceLocation;

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
}
