package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class SkillKick extends CFaerunAction
{
    public SkillKick()
    {
        super("faerunutils.skill.unarmed.kick");
        useTime = 1.5;
        staminaCost = 15;
        attributes.put(Attributes.BLUNT_DAMAGE, 30d);
        attributes.put(Attributes.INTERRUPT_FORCE, 30d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 110d);
        categoryTags.add("Unarmed");
    }
}
