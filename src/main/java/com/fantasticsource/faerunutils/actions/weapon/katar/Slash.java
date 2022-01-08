package com.fantasticsource.faerunutils.actions.weapon.katar;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunaction.katar.slash");

        useTime = 0.35;
        comboUsage = 30;
        staminaCost = 10;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE, 25));
//        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE, 0));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE, 2.25));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS, 2));

        //Using explicit mod amounts for damage tooltips, even if it's a 1x
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 0.65));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 0.065));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

//        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0.5));


        categoryTags.add("1H");
        categoryTags.add("Katar");


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
