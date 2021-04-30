package com.fantasticsource.faerunutils.actions.weapon.greatsword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunutils.greatsword.thrust");

        useTime = 1;
        comboUsage = 40;
        staminaCost = 10;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_RANGE, 4d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 45d);

        attributes.put(Attributes.INTERRUPT_FORCE, 45d);

        attributes.put(Attributes.FINESSE, 2d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 7d);


        categoryTags.add("2H");
        categoryTags.add("Greatsword");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.greatsword.slash");
        canComboTo.add("faerunutils.greatsword.thrust");
    }
}
