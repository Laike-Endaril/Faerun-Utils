package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class OverheadBash extends CFaerunAction
{
    public OverheadBash()
    {
        super("faerunaction.quarterstaff.overheadbash");

        useTime = 0.75;
        comboUsage = 40;
        staminaCost = 15;
        material = "wood";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE, 2.5));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE, 2.5));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE, 4));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS, 1));

        //Using explicit mod amounts for damage tooltips, even if it's a 1x
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 1.25));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 0.03));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 0.5));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0));


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.quarterstaff.jab");
        canComboTo.add("faerunaction.quarterstaff.spinningstrikes");
    }
}
