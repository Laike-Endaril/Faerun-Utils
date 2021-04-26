package com.fantasticsource.faerunutils;

import com.fantasticsource.mctools.betterattributes.AdditiveParentsAttribute;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.mctools.betterattributes.BonusPercentParentsAttribute;
import com.fantasticsource.tiamathud.CustomHUDData;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Attributes
{
    public static final BetterAttribute
            BODY_TEMPERATURE = new BetterAttribute(MODID + ".temperature", 0), //Custom scale, not C, F, K, etc.  Status effects and damage start at +-100.  Base value is comfort temp, current value is...current.
            COVERAGE = new BetterAttribute(MODID + ".coverage", 0), //Based on how much non-destroyed armor you're wearing.  100% with any full set of unbroken armor

    //All stats are balanced around a starting average of 10 and a max of 100 for each base attribute
    STRENGTH = new BetterAttribute(MODID + ".strength", 10), //Physical strength/force
            DEXTERITY = new BetterAttribute(MODID + ".dexterity", 10), //Physical skill/precision/reflexes
            CONSTITUTION = new BetterAttribute(MODID + ".constitution", 10), //Physical integrity/durability/resistance
            MAGICAL_FORCE = new BetterAttribute(MODID + ".magicalForce", 10), //Magical strength/force
            MAGICAL_SKILL = new BetterAttribute(MODID + ".magicalSkill", 10), //Magical skill/precision/reflexes
            MAGICAL_CONSTITUTION = new BetterAttribute(MODID + ".magicalConstitution", 10), //Magical integrity/durability/resistance

    THERMAL_RECOVERY = new AdditiveParentsAttribute(MODID + ".thermalRecovery", 5, new Pair<>(CONSTITUTION, 0.1), new Pair<>(MAGICAL_CONSTITUTION, 0.05)),

    //Meters (blocks) per second; average human RUNNING speed is ~5, world record would be a little over 10.4, both based on a 200m dash
    MOVE_SPEED = new AdditiveParentsAttribute(MODID + ".moveSpeed", 4.4, new Pair<>(STRENGTH, 0.02), new Pair<>(DEXTERITY, 0.02), new Pair<>(CONSTITUTION, 0.02)).setMCAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.01782642796),

    //As a multiplier; 1 for basic start, 4 for maxed
    ATTACK_SPEED = new AdditiveParentsAttribute(MODID + ".attackSpeed", 100, new Pair<>(STRENGTH, 1d), new Pair<>(DEXTERITY, 2d)),

    //Interrupt/knockback/trip force should have active modifiers per-attack/skill
    INTERRUPT_FORCE = new BonusPercentParentsAttribute(MODID + ".interruptForce", 0, STRENGTH),
            KNOCKBACK_FORCE = new BonusPercentParentsAttribute(MODID + ".knockbackForce", 0, STRENGTH),
            TRIP_FORCE = new BonusPercentParentsAttribute(MODID + ".tripForce", 0, STRENGTH),
            PHYSICAL_DAMAGE = new BonusPercentParentsAttribute(MODID + ".physicalDamage", 0, STRENGTH),
            SLASH_DAMAGE = new BetterAttribute(MODID + ".slashDamage", 0, PHYSICAL_DAMAGE),
            PIERCE_DAMAGE = new BetterAttribute(MODID + ".pierceDamage", 0, PHYSICAL_DAMAGE),
            BLUNT_DAMAGE = new BetterAttribute(MODID + ".bluntDamage", 0, PHYSICAL_DAMAGE),

    //Many of these will be altered further (often reduced) by modifiers on specific weapons and attacks
    PROJECTILE_ACCURACY = new AdditiveParentsAttribute(MODID + ".projectileAccuracy", 50, new Pair<>(DEXTERITY, 0.5)),
            FINESSE = new AdditiveParentsAttribute(MODID + ".finesse", 0, new Pair<>(DEXTERITY, 0.2)),
            BLOCK_CHANCE = new AdditiveParentsAttribute(MODID + ".block", 0, new Pair<>(DEXTERITY, 0.4)),
            PARRY_CHANCE = new AdditiveParentsAttribute(MODID + ".parry", 0, new Pair<>(DEXTERITY, 0.1)),
            DODGE_CHANCE = new AdditiveParentsAttribute(MODID + ".dodge", 0, new Pair<>(DEXTERITY, 0.4)),
            ARMOR_BYPASS_CHANCE = new BonusPercentParentsAttribute(MODID + ".armorBypass", 0, DEXTERITY),
            VITAL_STRIKE_CHANCE = new AdditiveParentsAttribute(MODID + ".vitalStrike", 0, new Pair<>(DEXTERITY, 0.5)),

    HEALTH = new AdditiveParentsAttribute(MODID + ".health", 300, new Pair<>(CONSTITUTION, 2d)).setMCAttribute(SharedMonsterAttributes.MAX_HEALTH, 1),
            HEALTH_REGEN = new AdditiveParentsAttribute(MODID + ".healthRegen", 0, new Pair<>(CONSTITUTION, 0.1)),
            STAMINA = new BetterAttribute(MODID + ".stamina", 100, CONSTITUTION),
            STAMINA_REGEN = new AdditiveParentsAttribute(MODID + ".staminaRegen", 5, new Pair<>(CONSTITUTION, 0.1)),
            STABILITY = new BetterAttribute(MODID + ".stability", 100, CONSTITUTION),
            INTERRUPT_STABILITY = new BetterAttribute(MODID + ".interruptStability", 0, STABILITY),
            KNOCKBACK_STABILITY = new BetterAttribute(MODID + ".knockbackStability", 0, STABILITY),
            TRIP_STABILITY = new BetterAttribute(MODID + ".tripStability", 0, STABILITY),

    CHEMICAL_DAMAGE = new BonusPercentParentsAttribute(MODID + ".chemicalDamage", 0, MAGICAL_FORCE),
            ACID_DAMAGE = new BetterAttribute(MODID + ".acidDamage", 0, CHEMICAL_DAMAGE),
            BIOLOGICAL_DAMAGE = new BetterAttribute(MODID + ".biologicalDamage", 0, CHEMICAL_DAMAGE),
            HEALING_DAMAGE = new BetterAttribute(MODID + ".healingDamage", 0, BIOLOGICAL_DAMAGE),
            POISON_DAMAGE = new BetterAttribute(MODID + ".poisonDamage", 0, BIOLOGICAL_DAMAGE),
            ENERGY_DAMAGE = new BonusPercentParentsAttribute(MODID + ".energyDamage", 0, MAGICAL_FORCE),
            ELECTRIC_DAMAGE = new BetterAttribute(MODID + ".electricDamage", 0, ENERGY_DAMAGE),
            THERMAL_DAMAGE = new BetterAttribute(MODID + ".thermalDamage", 0, ENERGY_DAMAGE),
            HEAT_DAMAGE = new BetterAttribute(MODID + ".heatDamage", 0, THERMAL_DAMAGE),
            COLD_DAMAGE = new BetterAttribute(MODID + ".coldDamage", 0, THERMAL_DAMAGE),

    CAST_SUCCESS_CHANCE = new BetterAttribute(MODID + ".castSuccess", 50, MAGICAL_SKILL),
            DISPEL_CHANCE = new AdditiveParentsAttribute(MODID + ".dispel", 0, new Pair<>(MAGICAL_SKILL, 0.5)),

    MANA = new BetterAttribute(MODID + ".mana", 100, MAGICAL_CONSTITUTION),
            MANA_REGEN = new AdditiveParentsAttribute(MODID + ".manaRegen", 5, new Pair<>(MAGICAL_CONSTITUTION, 0.1)),

    DEFENSE = new BetterAttribute(MODID + ".defense", 0),
            SLASH_DEFENSE = new BetterAttribute(MODID + ".slashDefense", 0, DEFENSE),
            PIERCE_DEFENSE = new BetterAttribute(MODID + ".pierceDefense", 0, DEFENSE),
            FALL_DEFENSE = new BetterAttribute(MODID + ".fallDefense", 0, DEFENSE),
            BLUNT_DEFENSE = new BetterAttribute(MODID + ".bluntDefense", 0, FALL_DEFENSE),

    ELEMENTAL_RESISTANCE = new BetterAttribute(MODID + ".elementalResist", 0),
            CHEMICAL_RESISTANCE = new BetterAttribute(MODID + ".chemicalResist", 0, ELEMENTAL_RESISTANCE),
            ACID_RESISTANCE = new BetterAttribute(MODID + ".acidResist", 0, CHEMICAL_RESISTANCE),
            BIOLOGICAL_RESISTANCE = new BetterAttribute(MODID + ".biologicalResist", 0, CHEMICAL_RESISTANCE),
            HEALING_RESISTANCE = new BetterAttribute(MODID + ".healingResist", 0, BIOLOGICAL_RESISTANCE),
            POISON_RESISTANCE = new BetterAttribute(MODID + ".poisonResist", 0, BIOLOGICAL_RESISTANCE),
            ENERGY_RESISTANCE = new BetterAttribute(MODID + ".energyResist", 0, ELEMENTAL_RESISTANCE),
            ELECTRIC_RESISTANCE = new BetterAttribute(MODID + ".electricResist", 0, ENERGY_RESISTANCE),
            THERMAL_RESISTANCE = new BetterAttribute(MODID + ".thermalResist", 0, ENERGY_RESISTANCE),
            HEAT_RESISTANCE = new BetterAttribute(MODID + ".heatResist", 0, THERMAL_RESISTANCE),
            COLD_RESISTANCE = new BetterAttribute(MODID + ".coldResist", 0, THERMAL_RESISTANCE),

    //All of these are determined by the attack/skill being used, and may or may not use other attributes depending
    RANGE = new BetterAttribute(MODID + ".range", 0), //Determined by the attack/skill being used
            MELEE_RANGE = new BetterAttribute(MODID + ".meleeRange", 0, RANGE),
            MIN_MELEE_RANGE = new BetterAttribute(MODID + ".minMeleeRange", 0, MELEE_RANGE),
            MAX_MELEE_RANGE = new BetterAttribute(MODID + ".maxMeleeRange", 0, MELEE_RANGE),
            PROJECTILE_RANGE = new BetterAttribute(MODID + ".projectileRange", 0, RANGE),
            MIN_PROJECTILE_RANGE = new BetterAttribute(MODID + ".minProjectileRange", 0, PROJECTILE_RANGE),
            MAX_PROJECTILE_RANGE = new BetterAttribute(MODID + ".maxProjectileRange", 0, PROJECTILE_RANGE),

    //All of these are determined by the attack/skill being used, and may or may not use other attributes depending
    MAX_TARGETS = new BetterAttribute(MODID + ".maxTargets", 0),
            MAX_MELEE_TARGETS = new BetterAttribute(MODID + ".maxMeleeTargets", 0, MAX_TARGETS), //Determined by the attack/skill being used, and possibly strength/dexterity/magical force/magical skill
            MAX_PROJECTILE_TARGETS = new BetterAttribute(MODID + ".maxProjectileTargets", 0, MAX_TARGETS), //Determined by the attack/skill being used

    //All of these are determined by the weapons/attack/skill being used, and may or may not use other attributes depending
    MAX_MELEE_ANGLE = new BetterAttribute(MODID + ".maxAngle", 0),
            COMBO = new BetterAttribute(MODID + ".combo", 100),
            PROJECTILE_SPEED = new BetterAttribute(MODID + ".projectileSpeed", 0),
            AOE_TIME = new BetterAttribute(MODID + ".aoeTime", 0),

    //Intelligence and Wisdom are terrible gameplay concepts *for players*...they work fine for NPCs, ONLY FOR DECIDING WHETHER AI MAKES GOOD DECISIONS, and you really only need one for that
    //On the other hand, memory is an often-overlooked concept for AI
    AI_INTELLIGENCE = new BetterAttribute(MODID + ".aiIntelligence", 0),
            AI_MEMORY = new BetterAttribute(MODID + ".aiMemory", 0);

    //Charisma is a terrible gameplay concept no matter what you apply it to, and I'm not using it period.


    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(Attributes.class);

        MIN_MELEE_RANGE.isGood = false;
        MIN_PROJECTILE_RANGE.isGood = false;
    }


    public static BetterAttribute getDefenseAttribute(BetterAttribute damageAttribute)
    {
        if (damageAttribute == Attributes.SLASH_DAMAGE) return Attributes.SLASH_DEFENSE;
        if (damageAttribute == Attributes.PIERCE_DAMAGE) return Attributes.PIERCE_DEFENSE;
        if (damageAttribute == Attributes.BLUNT_DAMAGE) return Attributes.BLUNT_DEFENSE;
        if (damageAttribute == Attributes.HEAT_DAMAGE) return Attributes.HEAT_RESISTANCE;
        if (damageAttribute == Attributes.COLD_DAMAGE) return Attributes.COLD_RESISTANCE;
        if (damageAttribute == Attributes.ELECTRIC_DAMAGE) return Attributes.ELECTRIC_RESISTANCE;
        if (damageAttribute == Attributes.POISON_DAMAGE) return Attributes.ACID_RESISTANCE;
        if (damageAttribute == Attributes.POISON_RESISTANCE) return Attributes.ACID_RESISTANCE;
        if (damageAttribute == Attributes.HEALING_DAMAGE) return Attributes.HEALING_RESISTANCE;
        if (damageAttribute == Attributes.PHYSICAL_DAMAGE) return Attributes.DEFENSE;
        if (damageAttribute == Attributes.THERMAL_DAMAGE) return Attributes.THERMAL_RESISTANCE;
        if (damageAttribute == Attributes.ENERGY_DAMAGE) return Attributes.ENERGY_RESISTANCE;
        if (damageAttribute == Attributes.BIOLOGICAL_DAMAGE) return Attributes.BIOLOGICAL_RESISTANCE;
        if (damageAttribute == Attributes.CHEMICAL_DAMAGE) return Attributes.CHEMICAL_RESISTANCE;
        return null;
    }


    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLivingBase)) return;


        HEALTH.getTotalAmount(entity);
        MOVE_SPEED.getTotalAmount(entity);

        COMBO.setCurrentAmount(entity, COMBO.getTotalAmount(entity));


        if (entity instanceof EntityPlayer && entity.world.isRemote)
        {
            for (BetterAttribute attribute : new BetterAttribute[]{STAMINA, MANA})
            {
                CustomHUDData.DATA.put(attribute.name.replace(MODID + ".", ""), "" + attribute.getCurrentAmount(entity));
                CustomHUDData.DATA.put("max" + attribute.name.replace(MODID + ".", ""), "" + attribute.getTotalAmount(entity));
            }
        }
    }

    @SubscribeEvent
    public static void entityUpdate(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase.world.isRemote || !livingBase.isEntityAlive()) return;


        double current = livingBase.getHealth(), dif = HEALTH_REGEN.getTotalAmount(livingBase) / 20;
        if (dif > 0)
        {
            current = Tools.min(HEALTH.getTotalAmount(livingBase), current + dif);
            livingBase.setHealth((float) current);
        }
        else if (dif < 0)
        {
            current = Tools.max(0, current + dif);
            livingBase.setHealth((float) current);
        }


        current = STAMINA.getCurrentAmount(livingBase);
        dif = livingBase.isSprinting() ? -0.125 : STAMINA_REGEN.getTotalAmount(livingBase) / 20;
        if (dif > 0)
        {
            current = Tools.min(STAMINA.getTotalAmount(livingBase), current + dif);
        }
        else if (dif < 0)
        {
            current = Tools.max(0, current + dif);
        }
        STAMINA.setCurrentAmount(livingBase, current);
        if (current <= 0 && livingBase instanceof EntityPlayerMP) livingBase.setSprinting(false);


        current = MANA.getCurrentAmount(livingBase);
        dif = MANA_REGEN.getTotalAmount(livingBase) / 20;
        if (dif > 0)
        {
            current = Tools.min(MANA.getTotalAmount(livingBase), current + dif);
        }
        else if (dif < 0)
        {
            current = Tools.max(0, current + dif);
        }
        MANA.setCurrentAmount(livingBase, current);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void attributeChange(BetterAttribute.BetterAttributeChangedEvent event)
    {
        Entity entity = event.entity;
        BetterAttribute attribute = event.attribute;

        if (!entity.world.isRemote)
        {
            if (attribute == STAMINA)
            {
                double speedMult = Tools.min(1, attribute.getCurrentAmount(entity) / attribute.getTotalAmount(entity) * 2);
                MOVE_SPEED.setBaseAmount(entity, MOVE_SPEED.defaultBaseAmount * speedMult);
                ATTACK_SPEED.setBaseAmount(entity, ATTACK_SPEED.defaultBaseAmount * speedMult);
            }
        }
        else
        {
            if (!(entity instanceof EntityPlayer)) return;

            if (attribute == STAMINA || attribute == MANA)
            {
                CustomHUDData.DATA.put(attribute.name.replace(MODID + ".", ""), "" + attribute.getCurrentAmount(event.entity));
                CustomHUDData.DATA.put("max" + attribute.name.replace(MODID + ".", ""), "" + attribute.getTotalAmount(event.entity));
            }
        }
    }

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        STAMINA.setCurrentAmount(livingBase, Tools.max(0, STAMINA.getCurrentAmount(livingBase) - 5));


        livingBase.motionY *= MOVE_SPEED.getTotalAmount(livingBase) / MOVE_SPEED.defaultBaseAmount;

        if (livingBase.isSprinting())
        {
            float f = livingBase.rotationYaw * 0.017453292F;
            livingBase.motionX += (double) (MathHelper.sin(f) * 0.2F);
            livingBase.motionZ -= (double) (MathHelper.cos(f) * 0.2F);
        }
    }
}
