package com.fantasticsource.faerunutils.actions.weapon.katar;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Thrust extends CFaerunAction
{
    public Thrust()
    {
        super("faerunutils.katar.thrust");

        useTime = 0.5;
        comboUsage = 30;
        staminaCost = 5;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_RANGE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 20d);

        attributes.put(Attributes.INTERRUPT_FORCE, 15d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 10d);

        attributes.put(Attributes.FINESSE, 2d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 10d);


        categoryTags.add("1H");
        categoryTags.add("Katar");


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

        canComboTo.add("faerunutils.sickle.stabbingswing");
        canComboTo.add("faerunutils.sickle.trippingslash");

        canComboTo.add("faerunutils.shield.bash");
    }
}
