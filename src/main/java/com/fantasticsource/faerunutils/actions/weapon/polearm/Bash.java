package com.fantasticsource.faerunutils.actions.weapon.polearm;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunutils.polearm.bash");

        useTime = 1;
        comboUsage = 60;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 30d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 30d);

        attributes.put(Attributes.INTERRUPT_FORCE, 50d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 150d);


        categoryTags.add("2H");
        categoryTags.add("Polearm");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.polearm.thrust");
    }
}
