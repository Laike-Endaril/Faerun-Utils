package com.fantasticsource.faerunutils.actions.weapon.sickle;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class TrippingSlash extends CFaerunAction
{
    public TrippingSlash()
    {
        super("faerunutils.sickle.trippingslash");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 20;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 5d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 0.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 2d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 25d);

        attributes.put(Attributes.INTERRUPT_FORCE, 5d);
        attributes.put(Attributes.TRIP_FORCE, 100d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 2.5d);


        categoryTags.add("1H");
        categoryTags.add("Sickle");


        canComboTo.add("faerunutils.unarmed.jab");
        canComboTo.add("faerunutils.unarmed.straight");
        canComboTo.add("faerunutils.unarmed.kick");

        canComboTo.add("faerunutils.sword.slash");
        canComboTo.add("faerunutils.sword.thrust");

        canComboTo.add("faerunutils.axe.chop");
        canComboTo.add("faerunutils.axe.overheadchop");

        canComboTo.add("faerunutils.dagger.slash");
        canComboTo.add("faerunutils.dagger.thrust");

        canComboTo.add("faerunutils.katar.slash");
        canComboTo.add("faerunutils.katar.thrust");

        canComboTo.add("faerunutils.mace.bash");
        canComboTo.add("faerunutils.mace.overheadbash");
    }
}
