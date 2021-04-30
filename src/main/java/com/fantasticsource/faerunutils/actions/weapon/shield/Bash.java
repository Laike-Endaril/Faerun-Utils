package com.fantasticsource.faerunutils.actions.weapon.shield;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunutils.shield.bash");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 10;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 2d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 10d);

        attributes.put(Attributes.INTERRUPT_FORCE, 50d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 150d);


        categoryTags.add("1H");
        categoryTags.add("Shield");


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
    }
}
