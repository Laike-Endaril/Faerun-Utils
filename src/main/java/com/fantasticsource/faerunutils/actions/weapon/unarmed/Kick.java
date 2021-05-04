package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Kick extends CFaerunAction
{
    public Kick()
    {
        super("faerunaction.unarmed.kick");

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


        canComboTo.add("faerunaction.unarmed.straight");

        canComboTo.add("faerunaction.sword.slash");

        canComboTo.add("faerunaction.axe.chop");
        canComboTo.add("faerunaction.axe.overheadchop");

        canComboTo.add("faerunaction.dagger.slash");
        canComboTo.add("faerunaction.dagger.thrust");

        canComboTo.add("faerunaction.mace.bash");

        canComboTo.add("faerunaction.spear.thrust");

        canComboTo.add("faerunaction.quarterstaff.jab");
        canComboTo.add("faerunaction.quarterstaff.spinningstrikes");

        canComboTo.add("faerunaction.greatsword.slash");
        canComboTo.add("faerunaction.greatsword.thrust");

        canComboTo.add("faerunaction.greataxe.slash");

        canComboTo.add("faerunaction.hammer.bash");

        canComboTo.add("faerunaction.bow.shot");
    }
}
