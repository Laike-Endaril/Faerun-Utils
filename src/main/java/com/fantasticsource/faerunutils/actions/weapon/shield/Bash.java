package com.fantasticsource.faerunutils.actions.weapon.shield;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Bash extends CFaerunAction
{
    public Bash()
    {
        super("faerunaction.shield.bash");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 10;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 2.5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 2d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.BLUNT_DAMAGE.name, 10d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 50d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 150d));


        categoryTags.add("1H");
        categoryTags.add("Shield");


        canComboTo.add("faerunaction.unarmed.straight");
        canComboTo.add("faerunaction.unarmed.kick");

        canComboTo.add("faerunaction.sword.slash");
        canComboTo.add("faerunaction.sword.thrust");

        canComboTo.add("faerunaction.axe.chop");
        canComboTo.add("faerunaction.axe.overheadchop");

        canComboTo.add("faerunaction.dagger.slash");
        canComboTo.add("faerunaction.dagger.thrust");

        canComboTo.add("faerunaction.katar.slash");
        canComboTo.add("faerunaction.katar.thrust");

        canComboTo.add("faerunaction.mace.bash");
        canComboTo.add("faerunaction.mace.overheadbash");

        canComboTo.add("faerunaction.sickle.stabbingswing");
        canComboTo.add("faerunaction.sickle.trippingslash");
    }
}