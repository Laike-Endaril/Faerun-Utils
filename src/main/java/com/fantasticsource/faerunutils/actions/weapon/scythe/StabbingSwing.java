package com.fantasticsource.faerunutils.actions.weapon.scythe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class StabbingSwing extends CFaerunAction
{
    public StabbingSwing()
    {
        super("faerunutils.scythe.stabbingswing");

        useTime = 1.25;
        comboUsage = 60;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 30d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 100d);

        attributes.put(Attributes.INTERRUPT_FORCE, 100d);

        attributes.put(Attributes.FINESSE, 3d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 5d);


        categoryTags.add("2H");
        categoryTags.add("Scythe");

        canComboTo.add("faerunutils.unarmed.kick");
    }
}
