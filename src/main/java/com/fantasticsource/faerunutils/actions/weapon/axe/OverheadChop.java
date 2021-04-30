package com.fantasticsource.faerunutils.actions.weapon.axe;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class OverheadChop extends CFaerunAction
{
    public OverheadChop()
    {
        super("faerunutils.axe.overheadchop");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 15;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 2.5d);
        attributes.put(Attributes.MIN_MELEE_RANGE, 1.5d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);

        attributes.put(Attributes.SLASH_DAMAGE, 20d);
        attributes.put(Attributes.PIERCE_DAMAGE, 20d);

        attributes.put(Attributes.INTERRUPT_FORCE, 25d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 10d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 3d);


        categoryTags.add("1H");
        categoryTags.add("Axe");


        canComboTo.add("faerunutils.unarmed.straight");

        canComboTo.add("faerunutils.sword.slash");
        canComboTo.add("faerunutils.sword.thrust");

        canComboTo.add("faerunutils.axe.chop");

        canComboTo.add("faerunutils.dagger.slash");
        canComboTo.add("faerunutils.dagger.thrust");

        canComboTo.add("faerunutils.katar.slash");
        canComboTo.add("faerunutils.katar.thrust");

        canComboTo.add("faerunutils.mace.bash");

        canComboTo.add("faerunutils.sickle.stabbingswing");
    }
}
