package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class SkillStraight extends CFaerunAction
{
    public SkillStraight()
    {
        super("faerunutils.skill.unarmed.straight");

        useTime = 0.5;
        comboUsage = 30;
        staminaCost = 5;
        material = "flesh";

        attributes.put(Attributes.MAX_MELEE_RANGE, 2d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);
        attributes.put(Attributes.BLUNT_DAMAGE, 10d);
        attributes.put(Attributes.INTERRUPT_FORCE, 10d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 10d);
        attributes.put(Attributes.FINESSE, 2.5d);

        categoryTags.add("Unarmed");

        canComboTo.add("faerunutils.skill.unarmed.jab");
        canComboTo.add("faerunutils.skill.unarmed.kick");
    }
}
