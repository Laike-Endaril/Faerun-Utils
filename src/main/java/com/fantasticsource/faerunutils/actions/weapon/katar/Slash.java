package com.fantasticsource.faerunutils.actions.weapon.katar;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;

public class Slash extends CFaerunAction
{
    public Slash()
    {
        super("faerunaction.katar.slash");

        useTime = 0.3;
        comboUsage = 30;
        staminaCost = 7;
        material = "metal";


        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_ANGLE.name, 20d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_RANGE.name, 2d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.MAX_MELEE_TARGETS.name, 1d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.SLASH_DAMAGE.name, 5d));

        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.FINESSE.name, 7d));
        attributeMods.add(new BetterAttributeMod(name + "0", Attributes.ARMOR_BYPASS_CHANCE.name, 2.5d));


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
