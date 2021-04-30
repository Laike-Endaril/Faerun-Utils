package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Kick extends CFaerunAction
{
    public Kick()
    {
        super("faerunutils.unarmed.kick");

        useTime = 1;
        comboUsage = 60;
        staminaCost = 15;
        material = "flesh";


        attributes.put(Attributes.MAX_MELEE_RANGE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 30d);

        attributes.put(Attributes.INTERRUPT_FORCE, 30d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 110d);

        attributes.put(Attributes.DODGE_CHANCE, -35d);
        attributes.put(Attributes.STABILITY, -50d);


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
    }
}
