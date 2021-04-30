package com.fantasticsource.faerunutils.actions.weapon.hammer;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class OverheadBash extends CFaerunAction
{
    public OverheadBash()
    {
        super("faerunutils.hammer.overheadbash");

        useTime = 1.5;
        comboUsage = 60;
        staminaCost = 30;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 100d);

        attributes.put(Attributes.INTERRUPT_FORCE, 200d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 15d);


        categoryTags.add("2H");
        categoryTags.add("Hammer");


        canComboTo.add("faerunutils.unarmed.kick");
    }
}
