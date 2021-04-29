package com.fantasticsource.faerunutils.actions.weapon.unarmed;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;

public class SkillKick extends CFaerunAction
{
    public SkillKick()
    {
        super("faerunutils.skill.unarmed.kick");

        useTime = 1;
        comboUsage = 60;
        staminaCost = 15;
        material = "flesh";

        attributes.put(Attributes.MAX_MELEE_RANGE, 2.5d);
        attributes.put(Attributes.MAX_MELEE_TARGETS, 1d);
        attributes.put(Attributes.BLUNT_DAMAGE, 30d);
        attributes.put(Attributes.INTERRUPT_FORCE, 30d);
        attributes.put(Attributes.KNOCKBACK_FORCE, 110d);
        attributes.put(Attributes.DODGE_CHANCE, -20d);
        attributes.put(Attributes.STABILITY, -50d);

        categoryTags.add("Unarmed");
        categoryTags.add("Heavy");
    }
}
