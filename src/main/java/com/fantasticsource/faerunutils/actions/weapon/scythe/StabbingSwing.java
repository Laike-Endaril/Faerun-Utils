package com.fantasticsource.faerunutils.actions.weapon.scythe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class StabbingSwing extends CFaerunAction
{
    public StabbingSwing()
    {
        super("faerunaction.scythe.stabbingswing");

        useTime = 1.25;
        comboUsage = 50;
        staminaCost = 20;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE, 25));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE, 3));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE, 4));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS, 1));

        //Using explicit mod amounts for damage tooltips, even if it's a 1x
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 2.5));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 0.5));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 0.25));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0.75));


        categoryTags.add("2H");
        categoryTags.add("Scythe");

        canComboTo.add("faerunaction.unarmed.kick");
    }
}
