package com.fantasticsource.faerunutils.actions.weapon.greataxe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunutils.greataxe.slash");

        useTime = 1;
        comboUsage = 40;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 45d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 3d);

        attributes.put(Attributes.SLASH_DAMAGE, 50d);

        attributes.put(Attributes.INTERRUPT_FORCE, 50d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 50d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 1d);


        categoryTags.add("2H");
        categoryTags.add("Greataxe");


        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.greataxe.slash");
        canComboTo.add("faerunutils.greataxe.overheadchop");
    }
}
