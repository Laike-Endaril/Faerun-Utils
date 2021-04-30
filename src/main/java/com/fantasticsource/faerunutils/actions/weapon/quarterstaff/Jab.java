package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Jab extends CFaerunAction
{
    public Jab()
    {
        super("faerunutils.quarterstaff.jab");

        useTime = 0.25;
        comboUsage = 20;
        staminaCost = 2.5;
        material = "wood";


        attributes.put(Attributes.MIN_MELEE_RANGE, 2d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 5d);

        attributes.put(Attributes.INTERRUPT_FORCE, 5d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 5d);

        attributes.put(Attributes.FINESSE, 25d);


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.quarterstaff.jab");
        canComboTo.add("faerunutils.quarterstaff.spinningstrikes");
        canComboTo.add("faerunutils.quarterstaff.overheadbash");
        canComboTo.add("faerunutils.quarterstaff.longhandstrike");
    }
}
