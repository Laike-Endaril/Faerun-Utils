package com.fantasticsource.faerunutils.actions.weapon.polearm;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunutils.polearm.thrust");

        useTime = 0.5;
        comboUsage = 30;
        staminaCost = 15;
        material = "metal";


        attributes.put(Attributes.MIN_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 15d);

        attributes.put(Attributes.INTERRUPT_FORCE, 15d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 50d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 10d);


        categoryTags.add("2H");
        categoryTags.add("Polearm");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.polearm.thrust");
        canComboTo.add("faerunutils.polearm.slash");
        canComboTo.add("faerunutils.polearm.bash");
    }
}
