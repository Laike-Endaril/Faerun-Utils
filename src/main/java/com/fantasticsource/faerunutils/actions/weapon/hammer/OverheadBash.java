package com.fantasticsource.faerunutils.actions.weapon.hammer;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class OverheadBash extends CFaerunAction
{
    public OverheadBash()
    {
        super("faerunutils.hammer.overheadbash");

        useTime = 1.5;
        comboUsage = 60;
        staminaCost = 30;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 3.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 100d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 200d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 15d));


        categoryTags.add("2H");
        categoryTags.add("Hammer");


        canComboTo.add("faerunutils.unarmed.kick");
    }
}
