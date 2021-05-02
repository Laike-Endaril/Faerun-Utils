package com.fantasticsource.faerunutils.actions.weapon.greataxe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class OverheadChop extends CFaerunAction
{
    public OverheadChop()
    {
        super("faerunutils.greataxe.overheadchop");

        useTime = 1.5;
        comboUsage = 60;
        staminaCost = 30;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 3.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.SLASH_DAMAGE.name, 40d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 40d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 100d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 20d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 1d));


        categoryTags.add("2H");
        categoryTags.add("Greataxe");


        canComboTo.add("faerunutils.unarmed.kick");
    }
}
