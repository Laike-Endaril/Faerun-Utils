package com.fantasticsource.faerunutils.actions.weapon.mace;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunutils.mace.bash");

        useTime = 0.5;
        comboUsage = 20;
        staminaCost = 5;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 22.5d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 1d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.BLUNT_DAMAGE, 15d);

        attributes.put(Attributes.INTERRUPT_FORCE, 10d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 110d);

        attributes.put(Attributes.FINESSE, 2.5d);


        categoryTags.add("1H");
        categoryTags.add("Mace");


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
