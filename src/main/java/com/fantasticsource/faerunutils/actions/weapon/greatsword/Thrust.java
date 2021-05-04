package com.fantasticsource.faerunutils.actions.weapon.greatsword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunaction.greatsword.thrust");

        useTime = 1;
        comboUsage = 40;
        staminaCost = 10;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 4d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 45d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 45d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 2d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 7d));


        categoryTags.add("2H");
        categoryTags.add("Greatsword");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.greatsword.slash");
        canComboTo.add("faerunaction.greatsword.thrust");
    }
}
