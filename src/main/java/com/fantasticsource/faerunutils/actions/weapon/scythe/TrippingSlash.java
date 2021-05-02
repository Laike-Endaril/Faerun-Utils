package com.fantasticsource.faerunutils.actions.weapon.scythe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class TrippingSlash extends CFaerunAction
{
    public TrippingSlash()
    {
        super("faerunutils.scythe.trippingslash");

        useTime = 1.5;
        comboUsage = 60;
        staminaCost = 25;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 4d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.SLASH_DAMAGE.name, 70d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 100d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.TRIP_FORCE.name, 150d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 2d));


        categoryTags.add("2H");
        categoryTags.add("Scythe");

        canComboTo.add("faerunutils.unarmed.kick");
    }
}
