package com.fantasticsource.faerunutils.actions.weapon.polearm;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunutils.polearm.bash");

        useTime = 1;
        comboUsage = 60;
        staminaCost = 20;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 30d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 3d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 4.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 30d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 50d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 150d));


        categoryTags.add("2H");
        categoryTags.add("Polearm");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.polearm.thrust");
    }
}
