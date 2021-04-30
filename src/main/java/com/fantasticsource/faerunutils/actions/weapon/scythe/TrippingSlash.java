package com.fantasticsource.faerunutils.actions.weapon.scythe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class TrippingSlash extends CFaerunAction
{
    public TrippingSlash()
    {
        super("faerunutils.scythe.trippingslash");

        useTime = 1.5;
        comboUsage = 60;
        staminaCost = 25;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 5d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 4d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.SLASH_DAMAGE, 70d);

        attributes.put(Attributes.INTERRUPT_FORCE, 100d);
        attributes.put(Attributes.TRIP_FORCE, 150d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 2d);


        categoryTags.add("2H");
        categoryTags.add("Scythe");

        canComboTo.add("faerunutils.unarmed.kick");
    }
}
