package com.fantasticsource.faerunutils.actions.weapon.sickle;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class TrippingSlash extends CFaerunAction
{
    public TrippingSlash()
    {
        super("faerunutils.sickle.trippingslash");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 20;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE.name, 0.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 2d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.PIERCE_DAMAGE.name, 25d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.TRIP_FORCE.name, 100d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 2.5d));


        categoryTags.add("1H");
        categoryTags.add("Sickle");


        canComboTo.add("faerunutils.unarmed.jab");
        canComboTo.add("faerunutils.unarmed.straight");
        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.sword.slash");
        canComboTo.add("faerunutils.sword.thrust");

        canComboTo.add("faerunutils.axe.chop");
        canComboTo.add("faerunutils.axe.overheadchop");

        canComboTo.add("faerunutils.dagger.slash");
        canComboTo.add("faerunutils.dagger.thrust");

        canComboTo.add("faerunutils.katar.slash");
        canComboTo.add("faerunutils.katar.thrust");

        canComboTo.add("faerunutils.mace.bash");
        canComboTo.add("faerunutils.mace.overheadbash");
    }
}
