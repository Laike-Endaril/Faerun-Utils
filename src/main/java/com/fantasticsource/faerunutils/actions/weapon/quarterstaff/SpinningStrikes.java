package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class SpinningStrikes extends CFaerunAction
{
    public SpinningStrikes()
    {
        super("faerunutils.quarterstaff.spinningstrikes");

        useTime = 0.25;
        comboUsage = 10;
        staminaCost = 2.5;
        material = "wood";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 2d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 15d));


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.quarterstaff.jab");
        canComboTo.add("faerunutils.quarterstaff.spinningstrikes");
        canComboTo.add("faerunutils.quarterstaff.overheadbash");
        canComboTo.add("faerunutils.quarterstaff.longhandstrike");
    }
}
