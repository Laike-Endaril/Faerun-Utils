package com.fantasticsource.faerunutils;

import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.actions.Cooldown;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.tiamatactions.action.ActionQueue;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamathud.CustomHUDData;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class Attributes
{
    public static final BetterAttribute
            BODY_TEMPERATURE = new BetterAttribute(MODID + ".temperature"), //Custom scale, not C, F, K, etc.  Status effects and damage start at +-100.  Base value is comfort temp, current value is...current.
            COVERAGE = new BetterAttribute(MODID + ".coverage"), //Based on how much non-destroyed armor you're wearing.  100% with any full set of unbroken armor

    //All stats are balanced around a starting average of 10 and a max of 20 for each base attribute
    STRENGTH = new BetterAttribute(MODID + ".strength", 10), //Physical strength/force
            DEXTERITY = new BetterAttribute(MODID + ".dexterity", 10), //Physical skill/precision/reflexes
            CONSTITUTION = new BetterAttribute(MODID + ".constitution", 10), //Physical integrity/durability/resistance
            MAGICAL_FORCE = new BetterAttribute(MODID + ".magicalForce", 10), //Magical strength/force
            MAGICAL_SKILL = new BetterAttribute(MODID + ".magicalSkill", 10), //Magical skill/precision/reflexes
            MAGICAL_CONSTITUTION = new BetterAttribute(MODID + ".magicalConstitution", 10), //Magical integrity/durability/resistance

    THERMAL_RECOVERY = new BetterAttribute(MODID + ".thermalRecovery", 0, 0, new Pair<>(CONSTITUTION, 0.25), new Pair<>(MAGICAL_CONSTITUTION, 0.25)),

    //Meters (blocks) per second; average human RUNNING speed is ~5, world record would be a little over 10.4, both based on a 200m dash
    MOVE_SPEED = new BetterAttribute(MODID + ".moveSpeed", 0, 0, new Pair<>(STRENGTH, 0.17), new Pair<>(DEXTERITY, 0.17), new Pair<>(CONSTITUTION, 0.17)).setMCAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.01782642796),

    //As a percentage; 100 for basic start, 200 for maxed
    ATTACK_SPEED = new BetterAttribute(MODID + ".attackSpeed", 0, 0, new Pair<>(STRENGTH, 3.3), new Pair<>(DEXTERITY, 6.7)),

    //Interrupt/knockback/trip force should have active modifiers per-attack/skill
    INTERRUPT_FORCE = new BetterAttribute(MODID + ".interruptForce", STRENGTH),
            KNOCKBACK_FORCE = new BetterAttribute(MODID + ".knockbackForce", STRENGTH),
            TRIP_FORCE = new BetterAttribute(MODID + ".tripForce", STRENGTH),
            PHYSICAL_DAMAGE = new BetterAttribute(MODID + ".physicalDamage", STRENGTH),
            SLASH_DAMAGE = new BetterAttribute(MODID + ".slashDamage", PHYSICAL_DAMAGE),
            PIERCE_DAMAGE = new BetterAttribute(MODID + ".pierceDamage", PHYSICAL_DAMAGE),
            BLUNT_DAMAGE = new BetterAttribute(MODID + ".bluntDamage", PHYSICAL_DAMAGE),

    //Many of these will be altered further by modifiers on specific weapons and attacks
    PROJECTILE_ACCURACY = new BetterAttribute(MODID + ".projectileAccuracy", 0, 0, new Pair<>(DEXTERITY, 5d)),
            BLOCK_CHANCE = new BetterAttribute(MODID + ".block", 0, 0, new Pair<>(DEXTERITY, 5d)),
            PARRY_CHANCE = new BetterAttribute(MODID + ".parry", 0, 0, new Pair<>(DEXTERITY, 2.5)),
            DODGE_CHANCE = new BetterAttribute(MODID + ".dodge", 0, 0, new Pair<>(DEXTERITY, 5d)),
            FINESSE = new BetterAttribute(MODID + ".finesse", DEXTERITY),
            ARMOR_BYPASS_CHANCE = new BetterAttribute(MODID + ".armorBypass", DEXTERITY),
            VITAL_STRIKE_CHANCE = new BetterAttribute(MODID + ".vitalStrike", 0, 0, new Pair<>(DEXTERITY, 0.5)),

    HEALTH = new BetterAttribute(MODID + ".health", 0, 0, new Pair<>(CONSTITUTION, 30d)).setMCAttribute(SharedMonsterAttributes.MAX_HEALTH, 1),
            HEALTH_REGEN = new BetterAttribute(MODID + ".healthRegen", 0, 0, new Pair<>(CONSTITUTION, 0.1)),
            STAMINA = new BetterAttribute(MODID + ".stamina", 0, 0, new Pair<>(CONSTITUTION, 10d)),
            STAMINA_REGEN = new BetterAttribute(MODID + ".staminaRegen", 0, 0, new Pair<>(CONSTITUTION, 0.5)),
            STABILITY = new BetterAttribute(MODID + ".stability", 0, 0, new Pair<>(CONSTITUTION, 10d)),
            INTERRUPT_STABILITY = new BetterAttribute(MODID + ".interruptStability", STABILITY),
            KNOCKBACK_STABILITY = new BetterAttribute(MODID + ".knockbackStability", STABILITY),
            TRIP_STABILITY = new BetterAttribute(MODID + ".tripStability", STABILITY),

    ELEMENTAL_DAMAGE = new BetterAttribute(MODID + ".elementalDamage", MAGICAL_FORCE),
            CHEMICAL_DAMAGE = new BetterAttribute(MODID + ".chemicalDamage", ELEMENTAL_DAMAGE),
            ACID_DAMAGE = new BetterAttribute(MODID + ".acidDamage", CHEMICAL_DAMAGE),
            BIOLOGICAL_DAMAGE = new BetterAttribute(MODID + ".biologicalDamage", CHEMICAL_DAMAGE),
            HEALING_DAMAGE = new BetterAttribute(MODID + ".healingDamage", BIOLOGICAL_DAMAGE),
            POISON_DAMAGE = new BetterAttribute(MODID + ".poisonDamage", BIOLOGICAL_DAMAGE),
            ENERGY_DAMAGE = new BetterAttribute(MODID + ".energyDamage", ELEMENTAL_DAMAGE),
            ELECTRIC_DAMAGE = new BetterAttribute(MODID + ".electricDamage", ENERGY_DAMAGE),
            THERMAL_DAMAGE = new BetterAttribute(MODID + ".thermalDamage", ENERGY_DAMAGE),
            HEAT_DAMAGE = new BetterAttribute(MODID + ".heatDamage", THERMAL_DAMAGE),
            COLD_DAMAGE = new BetterAttribute(MODID + ".coldDamage", THERMAL_DAMAGE),

    CAST_SUCCESS_CHANCE = new BetterAttribute(MODID + ".castSuccess", 0, 0, new Pair<>(MAGICAL_SKILL, 5d)),
            DISPEL_CHANCE = new BetterAttribute(MODID + ".dispel", 0, 0, new Pair<>(MAGICAL_SKILL, 0.5)),

    MANA = new BetterAttribute(MODID + ".mana", 100, MAGICAL_CONSTITUTION),
            MANA_REGEN = new BetterAttribute(MODID + ".manaRegen", 5, 0, new Pair<>(MAGICAL_CONSTITUTION, 0.1)),

    DEFENSE = new BetterAttribute(MODID + ".defense"),
            SLASH_DEFENSE = new BetterAttribute(MODID + ".slashDefense", DEFENSE),
            PIERCE_DEFENSE = new BetterAttribute(MODID + ".pierceDefense", DEFENSE),
            FALL_DEFENSE = new BetterAttribute(MODID + ".fallDefense", DEFENSE),
            BLUNT_DEFENSE = new BetterAttribute(MODID + ".bluntDefense", FALL_DEFENSE),

    ELEMENTAL_RESISTANCE = new BetterAttribute(MODID + ".elementalResist"),
            CHEMICAL_RESISTANCE = new BetterAttribute(MODID + ".chemicalResist", ELEMENTAL_RESISTANCE),
            ACID_RESISTANCE = new BetterAttribute(MODID + ".acidResist", CHEMICAL_RESISTANCE),
            BIOLOGICAL_RESISTANCE = new BetterAttribute(MODID + ".biologicalResist", CHEMICAL_RESISTANCE),
            HEALING_RESISTANCE = new BetterAttribute(MODID + ".healingResist", BIOLOGICAL_RESISTANCE),
            POISON_RESISTANCE = new BetterAttribute(MODID + ".poisonResist", BIOLOGICAL_RESISTANCE),
            ENERGY_RESISTANCE = new BetterAttribute(MODID + ".energyResist", ELEMENTAL_RESISTANCE),
            ELECTRIC_RESISTANCE = new BetterAttribute(MODID + ".electricResist", ENERGY_RESISTANCE),
            THERMAL_RESISTANCE = new BetterAttribute(MODID + ".thermalResist", ENERGY_RESISTANCE),
            HEAT_RESISTANCE = new BetterAttribute(MODID + ".heatResist", THERMAL_RESISTANCE),
            COLD_RESISTANCE = new BetterAttribute(MODID + ".coldResist", THERMAL_RESISTANCE),

    //All of these are determined by the attack/skill being used, and may or may not use other attributes depending
    RANGE = new BetterAttribute(MODID + ".range"), //Determined by the attack/skill being used
            MELEE_RANGE = new BetterAttribute(MODID + ".meleeRange", RANGE),
            MIN_MELEE_RANGE = new BetterAttribute(MODID + ".minMeleeRange", MELEE_RANGE),
            MAX_MELEE_RANGE = new BetterAttribute(MODID + ".maxMeleeRange", MELEE_RANGE),
            PROJECTILE_RANGE = new BetterAttribute(MODID + ".projectileRange", RANGE),
            MIN_PROJECTILE_RANGE = new BetterAttribute(MODID + ".minProjectileRange", PROJECTILE_RANGE),
            MAX_PROJECTILE_RANGE = new BetterAttribute(MODID + ".maxProjectileRange", PROJECTILE_RANGE),

    //All of these are determined by the attack/skill being used, and may or may not use other attributes depending
    MAX_TARGETS = new BetterAttribute(MODID + ".maxTargets"),
            MAX_MELEE_TARGETS = new BetterAttribute(MODID + ".maxMeleeTargets", MAX_TARGETS), //Determined by the attack/skill being used, and possibly strength/dexterity/magical force/magical skill
            MAX_PROJECTILE_TARGETS = new BetterAttribute(MODID + ".maxProjectileTargets", MAX_TARGETS), //Determined by the attack/skill being used

    //All of these are determined by the weapons/attack/skill being used, and may or may not use other attributes depending
    MAX_MELEE_ANGLE = new BetterAttribute(MODID + ".maxAngle"),
            COMBO = new BetterAttribute(MODID + ".combo", 100),
            PROJECTILE_COUNT = new BetterAttribute(MODID + ".projectileCount"),
            PROJECTILE_SPEED = new BetterAttribute(MODID + ".projectileSpeed"),
            PROJECTILE_DURATION = new BetterAttribute(MODID + ".projectileDuration"),
            AOE_DURATION = new BetterAttribute(MODID + ".aoeDuration"),

    //Intelligence and Wisdom are terrible gameplay concepts *for players*...they work fine for NPCs, ONLY FOR DECIDING WHETHER AI MAKES GOOD DECISIONS, and you really only need one for that
    //On the other hand, memory is an often-overlooked concept for AI
    AI_INTELLIGENCE = new BetterAttribute(MODID + ".aiIntelligence"),
            AI_MEMORY = new BetterAttribute(MODID + ".aiMemory");

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
        if (damageAttribute == Attributes.ACID_DAMAGE) return Attributes.ACID_RESISTANCE;
        if (damageAttribute == Attributes.POISON_DAMAGE) return Attributes.POISON_RESISTANCE;
        if (damageAttribute == Attributes.HEALING_DAMAGE) return Attributes.HEALING_RESISTANCE;
        if (damageAttribute == Attributes.ELECTRIC_DAMAGE) return Attributes.ELECTRIC_RESISTANCE;
        if (damageAttribute == Attributes.HEAT_DAMAGE) return Attributes.HEAT_RESISTANCE;
        if (damageAttribute == Attributes.COLD_DAMAGE) return Attributes.COLD_RESISTANCE;
        return null;
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void entityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLivingBase)) return;


        BetterAttributeMod.removeModsWithNameContaining(entity, "faerunaction", true);


        for (BetterAttribute attribute : BetterAttribute.BETTER_ATTRIBUTES.values()) attribute.setBaseAmount(entity, attribute.defaultBaseAmount);


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
        current = Tools.min(HEALTH.getTotalAmount(livingBase), current + dif);
        livingBase.setHealth((float) current);


        current = STAMINA.getCurrentAmount(livingBase);
        dif = livingBase.isSprinting() ? -0.125 : STAMINA_REGEN.getTotalAmount(livingBase) / 20;
        current = Tools.min(STAMINA.getTotalAmount(livingBase), current + dif);
        STAMINA.setCurrentAmount(livingBase, current);
        if (current <= 0) livingBase.setSprinting(false);
        else
        {
            ArrayList<CAction> actions = ActionQueue.get(livingBase, "Main").queue;
            if (actions != null && actions.size() > 0)
            {
                CAction action = actions.get(0);
                if (action instanceof CFaerunAction && action.getClass() != Cooldown.class) livingBase.setSprinting(false);
            }
        }


        current = MANA.getCurrentAmount(livingBase);
        dif = MANA_REGEN.getTotalAmount(livingBase) / 20;
        current = Tools.min(MANA.getTotalAmount(livingBase), current + dif);
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
                double speedMult = Tools.max(0.25, Tools.min(1, attribute.getCurrentAmount(entity) / attribute.getTotalAmount(entity) * 2));
                BetterAttributeMod.addMods(entity, new BetterAttributeMod("staminaSpeed", MOVE_SPEED, 200, 2, speedMult), new BetterAttributeMod("staminaSpeed", ATTACK_SPEED, 100, 2, speedMult));
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
}
