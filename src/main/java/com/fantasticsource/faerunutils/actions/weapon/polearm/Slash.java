package com.fantasticsource.faerunutils.actions.weapon.polearm;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunutils.polearm.slash");

        useTime = 1;
        comboUsage = 60;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 30d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 2d);

        attributes.put(Attributes.SLASH_DAMAGE, 25d);

        attributes.put(Attributes.INTERRUPT_FORCE, 30d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 30d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 3d);


        categoryTags.add("2H");
        categoryTags.add("Polearm");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.polearm.thrust");
    }
}
