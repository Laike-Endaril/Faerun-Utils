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
        super("faerunaction.bow.shot");

        useTime = 0.75;
        comboUsage = 30;
        staminaCost = 5;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_RANGE.name, Double.MAX_VALUE));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_DURATION.name, Double.MAX_VALUE));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_COUNT.name, 1));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_PROJECTILE_TARGETS.name, 1));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PROJECTILE_SPEED.name, 5));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 0.1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 2));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0.75));


        categoryTags.add("2H");
        categoryTags.add("Bow");


        canComboTo.add("faerunaction.unarmed.kick");
    }

    @Override
    public Entity getProjectile()
    {
        return new EntityTippedArrow(source.world, (EntityLivingBase) source);
    }
}
