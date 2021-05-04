package com.fantasticsource.faerunutils.actions.weapon.polearm;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunaction.polearm.thrust");

        useTime = 0.5;
        comboUsage = 30;
        staminaCost = 15;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 3d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 4.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 15d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 15d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 50d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 10d));


        categoryTags.add("2H");
        categoryTags.add("Polearm");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.polearm.thrust");
        canComboTo.add("faerunaction.polearm.slash");
        canComboTo.add("faerunaction.polearm.bash");
    }
}
