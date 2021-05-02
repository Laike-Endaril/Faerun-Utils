package com.fantasticsource.faerunutils.actions.weapon.hammer;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunutils.hammer.bash");

        useTime = 1;
        comboUsage = 40;
        staminaCost = 20;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 45d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 3d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 70d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 60d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 200d));


        categoryTags.add("2H");
        categoryTags.add("Hammer");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.hammer.bash");
        canComboTo.add("faerunutils.hammer.overheadbash");
    }
}
