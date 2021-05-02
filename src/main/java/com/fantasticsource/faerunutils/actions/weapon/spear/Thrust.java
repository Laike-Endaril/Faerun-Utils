package com.fantasticsource.faerunutils.actions.weapon.spear;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunutils.spear.thrust");

        useTime = 0.25;
        comboUsage = 25;
        staminaCost = 10;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 3d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 4.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 15d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 15d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 7d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 15d));


        categoryTags.add("2H");
        categoryTags.add("Spear");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.spear.thrust");
        canComboTo.add("faerunutils.spear.longthrust");
    }
}
