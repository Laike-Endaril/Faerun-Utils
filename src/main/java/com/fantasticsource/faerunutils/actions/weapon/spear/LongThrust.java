package com.fantasticsource.faerunutils.actions.weapon.spear;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class LongThrust extends CFaerunAction
{
    public LongThrust()
    {
        super("faerunutils.spear.longthrust");

        useTime = 0.75;
        comboUsage = 40;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MIN_MELEE_RANGE, 4d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 30d);

        attributes.put(Attributes.INTERRUPT_FORCE, 30d);

        attributes.put(Attributes.FINESSE, 2d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 10d);


        categoryTags.add("2H");
        categoryTags.add("Spear");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.spear.thrust");
        canComboTo.add("faerunutils.spear.longthrust");
    }
}
