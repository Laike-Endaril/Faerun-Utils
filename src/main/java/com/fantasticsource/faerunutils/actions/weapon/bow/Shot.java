package com.fantasticsource.faerunutils.actions.weapon.bow;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import net.minecraft.entity.projectile.EntityArrow;

public class Shot extends CFaerunAction
{
    public Shot()
    {
        super("faerunutils.bow.shot");

        useTime = 0.75;
        comboUsage = 30;
        staminaCost = 5;
        material = "metal";


        projectileType = EntityArrow.class;
        attributes.put(Attributes.PROJECTILE_RANGE, Double.MAX_VALUE);
        attributes.put(Attributes.PROJECTILE_DURATION, Double.MAX_VALUE);
        attributes.put(Attributes.PROJECTILE_SPEED, 5d);
        attributes.put(Attributes.MAX_PROJECTILE_TARGETS, 1d);

        attributes.put(Attributes.PIERCE_DAMAGE, 10d);

        attributes.put(Attributes.INTERRUPT_FORCE, 10d);

        attributes.put(Attributes.ARMOR_BYPASS_CHANCE, 5d);


        categoryTags.add("2H");
        categoryTags.add("Bow");


        canComboTo.add("faerunutils.unarmed.kick");
    }
}
