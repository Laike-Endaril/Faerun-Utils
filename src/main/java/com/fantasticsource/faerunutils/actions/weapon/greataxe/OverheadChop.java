package com.fantasticsource.faerunutils.actions.weapon.greataxe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class OverheadChop extends CFaerunAction
{
    public OverheadChop()
    {
        super("faerunutils.greataxe.overheadchop");

        useTime = 1.5;
        comboUsage = 60;
        staminaCost = 30;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.SLASH_DAMAGE, 40d);
        attributes.put(Attributes.PIERCE_DAMAGE, 40d);

        attributes.put(Attributes.INTERRUPT_FORCE, 100d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 20d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 1d);


        categoryTags.add("2H");
        categoryTags.add("Greataxe");


        canComboTo.add("faerunutils.unarmed.kick");
    }
}
