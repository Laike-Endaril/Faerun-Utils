package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.faerunutils.Attributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.util.math.MathHelper;

public abstract class CFaerunProjectileAction extends CFaerunAction
{
    public CFaerunProjectileAction()
    {
        super();
    }

    public CFaerunProjectileAction(String name)
    {
        super(name);
    }

    public abstract Entity getProjectile();

    @Override
    protected void doStuff()
    {
        int projectileCount = (int) Attributes.PROJECTILE_COUNT.getTotalAmount(source);
        double velocity = Attributes.PROJECTILE_SPEED.getTotalAmount(source);
        double inaccuracy = (1 - Attributes.PROJECTILE_ACCURACY.getTotalAmount(source) / 100) * 3;

        for (int i = 0; i < projectileCount; i++)
        {
            Entity projectile = getProjectile();


            Attributes.BODY_TEMPERATURE.setCurrentAmount(projectile, Attributes.BODY_TEMPERATURE.getCurrentAmount(source));

            Attributes.STRENGTH.setBaseAmount(projectile, Attributes.STRENGTH.getBaseAmount(source));
            Attributes.DEXTERITY.setBaseAmount(projectile, Attributes.DEXTERITY.getBaseAmount(source));
            Attributes.CONSTITUTION.setBaseAmount(projectile, Attributes.CONSTITUTION.getBaseAmount(source));
            Attributes.MAGICAL_FORCE.setBaseAmount(projectile, Attributes.MAGICAL_FORCE.getBaseAmount(source));
            Attributes.MAGICAL_SKILL.setBaseAmount(projectile, Attributes.MAGICAL_SKILL.getBaseAmount(source));
            Attributes.MAGICAL_CONSTITUTION.setBaseAmount(projectile, Attributes.MAGICAL_CONSTITUTION.getBaseAmount(source));

            Attributes.INTERRUPT_FORCE.setBaseAmount(projectile, Attributes.INTERRUPT_FORCE.getBaseAmount(source));
            Attributes.KNOCKBACK_FORCE.setBaseAmount(projectile, Attributes.KNOCKBACK_FORCE.getBaseAmount(source));
            Attributes.TRIP_FORCE.setBaseAmount(projectile, Attributes.TRIP_FORCE.getBaseAmount(source));
            Attributes.PHYSICAL_DAMAGE.setBaseAmount(projectile, Attributes.PHYSICAL_DAMAGE.getBaseAmount(source));
            Attributes.SLASH_DAMAGE.setBaseAmount(projectile, Attributes.SLASH_DAMAGE.getBaseAmount(source));
            Attributes.PIERCE_DAMAGE.setBaseAmount(projectile, Attributes.PIERCE_DAMAGE.getBaseAmount(source));
            Attributes.BLUNT_DAMAGE.setBaseAmount(projectile, Attributes.BLUNT_DAMAGE.getBaseAmount(source));

            Attributes.PROJECTILE_ACCURACY.setBaseAmount(projectile, Attributes.PROJECTILE_ACCURACY.getBaseAmount(source));
            Attributes.FINESSE.setBaseAmount(projectile, Attributes.FINESSE.getBaseAmount(source));
            Attributes.ARMOR_BYPASS_CHANCE.setBaseAmount(projectile, Attributes.ARMOR_BYPASS_CHANCE.getBaseAmount(source));
            Attributes.VITAL_STRIKE_CHANCE.setBaseAmount(projectile, Attributes.VITAL_STRIKE_CHANCE.getBaseAmount(source));

            Attributes.ELEMENTAL_DAMAGE.setBaseAmount(projectile, Attributes.ELEMENTAL_DAMAGE.getBaseAmount(source));
            Attributes.CHEMICAL_DAMAGE.setBaseAmount(projectile, Attributes.CHEMICAL_DAMAGE.getBaseAmount(source));
            Attributes.ACID_DAMAGE.setBaseAmount(projectile, Attributes.ACID_DAMAGE.getBaseAmount(source));
            Attributes.BIOLOGICAL_DAMAGE.setBaseAmount(projectile, Attributes.BIOLOGICAL_DAMAGE.getBaseAmount(source));
            Attributes.HEALING_DAMAGE.setBaseAmount(projectile, Attributes.HEALING_DAMAGE.getBaseAmount(source));
            Attributes.POISON_DAMAGE.setBaseAmount(projectile, Attributes.POISON_DAMAGE.getBaseAmount(source));
            Attributes.ENERGY_DAMAGE.setBaseAmount(projectile, Attributes.ENERGY_DAMAGE.getBaseAmount(source));
            Attributes.ELECTRIC_DAMAGE.setBaseAmount(projectile, Attributes.ELECTRIC_DAMAGE.getBaseAmount(source));
            Attributes.THERMAL_DAMAGE.setBaseAmount(projectile, Attributes.THERMAL_DAMAGE.getBaseAmount(source));
            Attributes.HEAT_DAMAGE.setBaseAmount(projectile, Attributes.HEAT_DAMAGE.getBaseAmount(source));
            Attributes.COLD_DAMAGE.setBaseAmount(projectile, Attributes.COLD_DAMAGE.getBaseAmount(source));

            Attributes.RANGE.setBaseAmount(projectile, Attributes.RANGE.getBaseAmount(source));
            Attributes.PROJECTILE_RANGE.setBaseAmount(projectile, Attributes.PROJECTILE_RANGE.getBaseAmount(source));
            Attributes.MIN_PROJECTILE_RANGE.setBaseAmount(projectile, Attributes.MIN_PROJECTILE_RANGE.getBaseAmount(source));
            Attributes.MAX_PROJECTILE_RANGE.setBaseAmount(projectile, Attributes.MAX_PROJECTILE_RANGE.getBaseAmount(source));

            Attributes.MAX_TARGETS.setBaseAmount(projectile, Attributes.MAX_TARGETS.getBaseAmount(source));
            Attributes.MAX_PROJECTILE_TARGETS.setBaseAmount(projectile, Attributes.MAX_PROJECTILE_TARGETS.getBaseAmount(source));

            Attributes.PROJECTILE_COUNT.setBaseAmount(projectile, Attributes.PROJECTILE_COUNT.getBaseAmount(source));
            Attributes.PROJECTILE_SPEED.setBaseAmount(projectile, Attributes.PROJECTILE_SPEED.getBaseAmount(source));
            Attributes.PROJECTILE_DURATION.setBaseAmount(projectile, Attributes.PROJECTILE_DURATION.getBaseAmount(source));
            Attributes.AOE_DURATION.setBaseAmount(projectile, Attributes.AOE_DURATION.getBaseAmount(source));


            float yaw = source.getRotationYawHead(), pitch = source.rotationPitch;
            float x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
            float y = -MathHelper.sin(pitch * 0.017453292F);
            float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
            ((IProjectile) projectile).shoot(x, y, z, (float) velocity, (float) inaccuracy);
            projectile.motionX += source.motionX;
            projectile.motionY += source.motionY;
            projectile.motionZ += source.motionZ;
        }
    }
}
