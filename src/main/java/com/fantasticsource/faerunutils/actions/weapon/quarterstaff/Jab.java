package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Jab extends CFaerunAction
{
    public Jab()
    {
        super("faerunaction.quarterstaff.jab");

        useTime = 0.25;
        comboUsage = 20;
        staminaCost = 2.5;
        material = "wood";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 2d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 3.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 25d));


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.quarterstaff.jab");
        canComboTo.add("faerunaction.quarterstaff.spinningstrikes");
        canComboTo.add("faerunaction.quarterstaff.overheadbash");
        canComboTo.add("faerunaction.quarterstaff.longhandstrike");
    }
}
