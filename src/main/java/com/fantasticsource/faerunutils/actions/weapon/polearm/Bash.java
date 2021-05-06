package com.fantasticsource.faerunutils.actions.weapon.polearm;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunaction.polearm.bash");

        useTime = 0.75;
        comboUsage = 30;
        staminaCost = 20;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE, 35));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE, 3));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE, 4));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS, 1));

        //Using explicit mod amounts for damage tooltips, even if it's a 1x
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 1.5));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 0.15));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 0.25));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0));


        categoryTags.add("2H");
        categoryTags.add("Polearm");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.polearm.thrust");
    }
}
