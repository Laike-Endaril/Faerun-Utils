package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.animations.AniKick;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Kick extends CFaerunAction
{
    public Kick()
    {
        super("faerunaction.unarmed.kick");

        useTime = 1;
        comboUsage = 50;
        staminaCost = 20;
        material = "flesh";
        animationsToUse = new Class[]{AniKick.class};
        selfInterruptible = false;
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.MOVE_SPEED, 200, 2, 0));


//        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE, 0));
//        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MIN_MELEE_RANGE, 0));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE, 2.5));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS, 1));

        //Using explicit mod amounts for damage tooltips, even if it's a 1x
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.SLASH_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.PIERCE_DAMAGE, 2, 0));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.BLUNT_DAMAGE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ELEMENTAL_DAMAGE, 2, 0));

        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.INTERRUPT_FORCE, 2, 0.25));
//        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.KNOCKBACK_FORCE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.TRIP_FORCE, 2, 0));

//        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.FINESSE, 2, 1));
        attributeMods.add(new BetterAttributeMod(name + "2", Attributes.ARMOR_BYPASS_CHANCE, 2, 0));


        categoryTags.add("1L");
        categoryTags.add("Unarmed");
        categoryTags.add("Heavy");


        canComboTo.add("faerunaction.unarmed.straight");

        canComboTo.add("faerunaction.sword.slash");

        canComboTo.add("faerunaction.axe.chop");
        canComboTo.add("faerunaction.axe.overheadchop");

        canComboTo.add("faerunaction.dagger.slash");
        canComboTo.add("faerunaction.dagger.thrust");

        canComboTo.add("faerunaction.mace.bash");

        canComboTo.add("faerunaction.spear.thrust");

        canComboTo.add("faerunaction.quarterstaff.jab");
        canComboTo.add("faerunaction.quarterstaff.spinningstrikes");

        canComboTo.add("faerunaction.greatsword.slash");
        canComboTo.add("faerunaction.greatsword.thrust");

        canComboTo.add("faerunaction.greataxe.slash");

        canComboTo.add("faerunaction.hammer.bash");

        canComboTo.add("faerunaction.bow.shot");
    }


    @Override
    protected void initHitTime()
    {
        hitTime = useTime * 0.5;
    }
}
