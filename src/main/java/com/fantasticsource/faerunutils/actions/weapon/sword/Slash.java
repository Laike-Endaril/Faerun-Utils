package com.fantasticsource.faerunutils.actions.weapon.sword;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunutils.sword.slash");

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
