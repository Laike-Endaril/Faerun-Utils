package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class SpinningStrikes extends CFaerunAction
{
    public SpinningStrikes()
    {
        super("faerunaction.quarterstaff.spinningstrikes");

        useTime = 0.25;
        comboUsage = 20;
        staminaCost = 5;
        material = "wood";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE, 45));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE, 1.5));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE, 3.5));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS, 2));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 0.5));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 0.01));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 0.01));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

//        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0));


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.quarterstaff.jab");
        canComboTo.add("faerunaction.quarterstaff.spinningstrikes");
        canComboTo.add("faerunaction.quarterstaff.overheadbash");
        canComboTo.add("faerunaction.quarterstaff.longhandstrike");
    }
}
