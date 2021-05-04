package com.fantasticsource.faerunutils.actions.weapon.greatsword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunaction.greatsword.slash");

        useTime = 1;
        comboUsage = 30;
        staminaCost = 10;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 45d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 3.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 4d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.SLASH_DAMAGE.name, 35d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 50d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 75d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 3d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 3d));


        categoryTags.add("2H");
        categoryTags.add("Greatsword");


        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.greatsword.slash");
        canComboTo.add("faerunaction.greatsword.thrust");
    }
}
