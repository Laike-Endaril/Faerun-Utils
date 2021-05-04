package com.fantasticsource.faerunutils.actions.weapon.spear;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class LongThrust extends CFaerunAction
{
    public LongThrust()
    {
        super("faerunaction.spear.longthrust");

        useTime = 0.75;
        comboUsage = 40;
        staminaCost = 20;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 4d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 30d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 30d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 2d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 10d));


        categoryTags.add("2H");
        categoryTags.add("Spear");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.spear.thrust");
        canComboTo.add("faerunaction.spear.longthrust");
    }
}
