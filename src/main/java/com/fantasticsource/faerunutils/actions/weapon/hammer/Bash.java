package com.fantasticsource.faerunutils.actions.weapon.hammer;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunutils.hammer.bash");

        useTime = 1;
        comboUsage = 40;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 45d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 70d);

        attributes.put(Attributes.INTERRUPT_FORCE, 60d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 200d);


        categoryTags.add("2H");
        categoryTags.add("Hammer");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.hammer.bash");
        canComboTo.add("faerunutils.hammer.overheadbash");
    }
}
