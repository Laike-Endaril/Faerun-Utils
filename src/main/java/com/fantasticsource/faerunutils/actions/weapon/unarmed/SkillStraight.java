package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class SkillStraight extends CFaerunAction
{
    public SkillStraight()
    {
        super("faerunutils.skill.unarmed.straight");
        useTime = 0.5;
        staminaCost = 5;
        attributes.put(Attributes.BLUNT_DAMAGE, 10d);
        attributes.put(Attributes.INTERRUPT_FORCE, 10d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 10d);
        categoryTags.add("Unarmed");
        canComboTo.add("faerunutils.skill.unarmed.jab");
        canComboTo.add("faerunutils.skill.unarmed.kick");
    }
}
