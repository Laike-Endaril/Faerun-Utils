package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class OverheadBash extends CFaerunAction
{
    public OverheadBash()
    {
        super("faerunutils.quarterstaff.overheadbash");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 15;
        material = "wood";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 30d);

        attributes.put(Attributes.INTERRUPT_FORCE, 25d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 5d);


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.quarterstaff.jab");
        canComboTo.add("faerunutils.quarterstaff.spinningstrikes");
    }
}
