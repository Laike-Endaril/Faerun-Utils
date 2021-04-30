package com.fantasticsource.faerunutils.actions.weapon.sword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunutils.sword.slash");

        useTime = 0.5;
        comboUsage = 20;
        staminaCost = 5;
        material = "metal";


        attributes.put(Attributes.MAX_MELEE_ANGLE, 35d);
        attributes.put(Attributes.MAX_MELEE_RANGE, 3d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 2d);

        attributes.put(Attributes.SLASH_DAMAGE, 15d);

        attributes.put(Attributes.INTERRUPT_FORCE, 5d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 5d);

        attributes.put(Attributes.FINESSE, 5d);
        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 5d);


        categoryTags.add("1H");
        categoryTags.add("Sword");


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
