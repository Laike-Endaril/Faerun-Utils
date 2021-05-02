package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Kick extends CFaerunAction
{
    public Kick()
    {
        super("faerunutils.unarmed.kick");

        useTime = 1;
        comboUsage = 60;
        staminaCost = 15;
        material = "flesh";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 30d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 30d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 110d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.DODGE_CHANCE.name, -35d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.STABILITY.name, -50d));


        categoryTags.add("1L");
        categoryTags.add("Unarmed");
        categoryTags.add("Heavy");


        canComboTo.add("faerunutils.unarmed.straight");

        canComboTo.add("faerunutils.sword.slash");

        canComboTo.add("faerunutils.axe.chop");
        canComboTo.add("faerunutils.axe.overheadchop");

        canComboTo.add("faerunutils.dagger.slash");
        canComboTo.add("faerunutils.dagger.thrust");

        canComboTo.add("faerunutils.mace.bash");

        canComboTo.add("faerunutils.spear.thrust");

        canComboTo.add("faerunutils.quarterstaff.jab");
        canComboTo.add("faerunutils.quarterstaff.spinningstrikes");

        canComboTo.add("faerunutils.greatsword.slash");
        canComboTo.add("faerunutils.greatsword.thrust");

        canComboTo.add("faerunutils.greataxe.slash");

        canComboTo.add("faerunutils.hammer.bash");

        canComboTo.add("faerunutils.bow.shot");
    }
}
