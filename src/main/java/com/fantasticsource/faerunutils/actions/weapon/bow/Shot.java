package com.fantasticsource.faerunutils.actions.weapon.bow;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunProjectileAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;

public class Shot extends CFaerunProjectileAction
{
    public Shot()
    {
        super("faerunutils.bow.shot");

        useTime = 0.75;
        comboUsage = 30;
        staminaCost = 5;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_RANGE.name, Double.MAX_VALUE));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_DURATION.name, Double.MAX_VALUE));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_COUNT.name, 1d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_PROJECTILE_TARGETS.name, 1d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_SPEED.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 10d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 10d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 5d));


        categoryTags.add("2H");
        categoryTags.add("Bow");


        canComboTo.add("faerunutils.unarmed.kick");
    }

    @Override
    public Entity getProjectile()
    {
        return new EntityTippedArrow(source.world, (EntityLivingBase) source);
    }
}
