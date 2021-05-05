package com.fantasticsource.faerunutils.actions.weapon.sword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunaction.sword.slash");

        useTime = 0.5;
        comboUsage = 20;
        staminaCost = 5;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 35d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 3d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 2d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.SLASH_DAMAGE.name, 15d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.INTERRUPT_FORCE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.KNOCKBACK_FORCE.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 5d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 5d));


        categoryTags.add("1H");
        categoryTags.add("Sword");


        canComboTo.add("faerunaction.unarmed.jab");
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

        canComboTo.add("faerunaction.shield.bash");
    }
}
