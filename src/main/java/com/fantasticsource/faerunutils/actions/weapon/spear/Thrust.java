package com.fantasticsource.faerunutils.actions.weapon.spear;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunutils.spear.thrust");

        useTime = 0.25;
        comboUsage = 25;
        staminaCost = 10;
        material = "metal";


        attributes.put(Attributes.MIN_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 15d);

        attributes.put(Attributes.INTERRUPT_FORCE, 15d);

        attributes.put(Attributes.FINESSE, 7d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 15d);


        categoryTags.add("2H");
        categoryTags.add("Spear");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.spear.thrust");
        canComboTo.add("faerunutils.spear.longthrust");
    }
}
