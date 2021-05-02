package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class OverheadBash extends CFaerunAction
{
    public OverheadBash()
    {
        super("faerunutils.quarterstaff.overheadbash");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 15;
        material = "wood";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 4d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 30d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 25d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 5d));


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.quarterstaff.jab");
        canComboTo.add("faerunutils.quarterstaff.spinningstrikes");
    }
}
