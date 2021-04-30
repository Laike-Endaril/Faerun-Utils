package com.fantasticsource.faerunutils.actions.weapon.greatsword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunutils.greatsword.slash");

        useTime = 1;
        comboUsage = 30;
        staminaCost = 10;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 45d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 4d);

        attributes.put(Attributes.SLASH_DAMAGE, 35d);

        attributes.put(Attributes.INTERRUPT_FORCE, 50d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 75d);

        attributes.put(Attributes.FINESSE, 3d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 3d);


        categoryTags.add("2H");
        categoryTags.add("Greatsword");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.greatsword.slash");
        canComboTo.add("faerunutils.greatsword.thrust");
    }
}
