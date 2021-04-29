package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class SkillJab extends CFaerunAction
{
    public SkillJab()
    {
        super("faerunutils.skill.unarmed.jab");

        useTime = 0.25;
        comboUsage = 20;
        staminaCost = 2.5;

        attributes.put(Attributes.MAX_MELEE_RANGE, 2d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);
        attributes.put(Attributes.BLUNT_DAMAGE, 5d);
        attributes.put(Attributes.INTERRUPT_FORCE, 5d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 5d);
        attributes.put(Attributes.FINESSE, 5d);

        categoryTags.add("Unarmed");

        canComboTo.add("faerunutils.skill.unarmed.jab");
        canComboTo.add("faerunutils.skill.unarmed.straight");
        canComboTo.add("faerunutils.skill.unarmed.kick");
    }
}
