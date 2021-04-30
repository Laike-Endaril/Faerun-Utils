package com.fantasticsource.faerunutils.actions.weapon.quarterstaff;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class LonghandStrike extends CFaerunAction
{
    public LonghandStrike()
    {
        super("faerunutils.quarterstaff.longhandstrike");

        useTime = 0.5;
        comboUsage = 40;
        staminaCost = 10;
        material = "wood";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 45d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 20d);

        attributes.put(Attributes.INTERRUPT_FORCE, 20d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 50d);


        categoryTags.add("2H");
        categoryTags.add("Quarterstaff");


        canComboTo.add("faerunutils.unarmed.kick");
    }
}
