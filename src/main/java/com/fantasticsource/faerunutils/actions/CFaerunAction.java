package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.mctools.EntityFilters;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatactions.config.TiamatActionsConfig;
import com.fantasticsource.tiamatactions.node.CNode;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.DecimalWeightedPool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CFaerunAction extends CAction
{
    public double useTime = 0, hpCost = 0, mpCost = 0, staminaCost = 0, timer = 0;
    public LinkedHashMap<BetterAttribute, Double> attributes = new LinkedHashMap<>();
    public ArrayList<String> categoryTags = new ArrayList<>(), canComboTo = new ArrayList<>();

    //See Attributes class, there are many, including damage types dealt
    public ArrayList<AttributeModifier> activeAttributeModifiers = new ArrayList<>();

    public CFaerunAction()
    {
        super();
    }

    public CFaerunAction(String name)
    {
        super(name);
    }

    public String getTooltip()
    {
        StringBuilder builder = new StringBuilder(name);

        if (useTime > 0) builder.append("\n" + TextFormatting.YELLOW + "Use Time: " + Tools.formatNicely(useTime) + "s");
        if (hpCost > 0) builder.append("\n" + TextFormatting.RED + "HP Cost: " + Tools.formatNicely(hpCost));
        if (mpCost > 0) builder.append("\n" + TextFormatting.BLUE + "MP Cost: " + Tools.formatNicely(mpCost));
        if (staminaCost > 0) builder.append("\n" + TextFormatting.GOLD + "Stamina Cost: " + Tools.formatNicely(staminaCost));

        if (categoryTags.size() > 0)
        {
            builder.append("\nCategories:");
            for (String category : categoryTags) builder.append("\n" + TextFormatting.LIGHT_PURPLE + category);
        }

        return builder.toString();
    }


    protected void execute(Entity source, String event)
    {
        Profiler profiler = source.world.profiler;

        boolean profile = TiamatActionsConfig.serverSettings.profilingMode.equals("actions");
        if (profile) profiler.startSection("Action: " + name);
        if (profile) profiler.startSection("Event: " + event);

        HashMap<Long, Object> results = new HashMap<>();
        switch (event)
        {
            case "init":
                for (CNode endNode : initEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                break;

            case "start":
                for (Map.Entry<BetterAttribute, Double> entry : attributes.entrySet())
                {
                    BetterAttribute attribute = entry.getKey();
                    attribute.setBaseAmount(source, attribute.getBaseAmount(source) + entry.getValue());
                }
                for (CNode endNode : startEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                break;

            case "tick":
                for (CNode endNode : tickEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                timer += Attributes.ATTACK_SPEED.getTotalAmount(source) * 0.05;
                if (timer >= useTime) active = false;
                break;

            case "end":
                onCompletion();
                for (Map.Entry<BetterAttribute, Double> entry : attributes.entrySet())
                {
                    BetterAttribute attribute = entry.getKey();
                    attribute.setBaseAmount(source, attribute.getBaseAmount(source) - entry.getValue());
                }
                for (CNode endNode : endEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results, true);
                break;
        }

        if (profile) profiler.endSection();
        if (profile) profiler.endSection();
    }

    protected void onCompletion()
    {
        int targets = (int) Attributes.MAX_TARGETS.getTotalAmount(source);
        if (targets == 0) return;


        ArrayList<Entity> entities = new ArrayList<>(source.world.loadedEntityList);
        double minRange = Attributes.MIN_MELEE_RANGE.getTotalAmount(source);
        Vec3d sourceCenter = source.getPositionVector().addVector(0, source.height * 0.5, 0);
        if (minRange > 0)
        {
            double minSqr = minRange * minRange;
            entities.removeIf(entity ->
            {
                if (!entity.isEntityAlive() || !(entity instanceof EntityLivingBase)) return true;
                return entity.getPositionVector().addVector(0, entity.height * 0.5, 0).squareDistanceTo(sourceCenter) > minSqr;
            });
        }

        double finesse = Attributes.FINESSE.getTotalAmount(source);
        for (Entity entity : EntityFilters.inCone(sourceCenter, source.getRotationYawHead(), source.rotationPitch, Attributes.MAX_MELEE_RANGE.getTotalAmount(source), Attributes.MAX_MELEE_ANGLE.getTotalAmount(source), true, entities))
        {
            if (Sight.canSee((EntityLivingBase) entity, source, true))
            {
                if (FaerunUtils.canBlock(entity) && Math.random() < Attributes.BLOCK_CHANCE.getTotalAmount(entity) / 100d)
                {
                    //TODO block indicators
                    return;
                }

                if (Math.random() < (Attributes.PARRY_CHANCE.getTotalAmount(entity) - finesse) / 100d)
                {
                    //TODO parry indicators
                    finesse *= 0.5;
                    targets--;
                    continue;
                }

                if (Math.random() < (Attributes.DODGE_CHANCE.getTotalAmount(entity) - finesse) / 100d)
                {
                    //TODO dodge indicators
                    continue;
                }
            }

            onHit(entity);
            if (--targets == 0) break;
        }
    }

    protected void onHit(Entity entity)
    {
        boolean useArmor = Math.random() < (Attributes.COVERAGE.getTotalAmount(entity) - Attributes.ARMOR_BYPASS_CHANCE.getTotalAmount(source)) / 100d;
        DecimalWeightedPool<ItemStack> armorCoverage = new DecimalWeightedPool<>();
        boolean vitalStrike = Math.random() < Attributes.VITAL_STRIKE_CHANCE.getTotalAmount(source);

        //Apply active armor mods
        if (useArmor)
        {
            ArrayList<ItemStack> armors = GlobalInventory.getVanillaArmorItems(entity);
            armors.addAll(GlobalInventory.getTiamatArmor(entity));
            for (ItemStack armor : armors)
            {
                if (!armor.hasTagCompound()) continue;
                double coverage = armor.getTagCompound().getDouble("coverage");
                if (coverage <= 0) continue;

                armorCoverage.setWeight(armor, coverage);

                NBTTagCompound compound = MCTools.getSubCompoundIfExists(armor.getTagCompound(), "tiamatitems", "generic");
                if (compound != null)
                {
                    for (String key : compound.getKeySet())
                    {
                        BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(key);
                        if (attributes != null) attribute.setBaseAmount(entity, attribute.getBaseAmount(entity) + compound.getDouble(key));
                    }
                }
            }
        }

        //Deal damage
        ItemStack armorToDamage = armorCoverage.getRandom();
        for (Map.Entry<BetterAttribute, Double> entry : attributes.entrySet())
        {
            BetterAttribute damageAttribute = entry.getKey(), defenseAttribute = Attributes.getDefenseAttribute(damageAttribute);
            if (defenseAttribute == null) continue;

            double amount = entry.getValue();
            if (amount == 0) continue;

            double prevented = defenseAttribute.getTotalAmount(entity);

            if (useArmor && armorToDamage != null && prevented > 0)
            {
                double armorDamage = prevented;
                NBTTagCompound compound = MCTools.getSubCompoundIfExists(armorToDamage.getTagCompound(), "tiamatitems", "generic");
                if (compound != null && compound.hasKey("armor." + damageAttribute.name + ".reduction")) armorDamage -= compound.getDouble("armor." + damageAttribute.name + ".reduction");
                if (armorDamage > 0) armorToDamage.damageItem((int) armorDamage, (EntityLivingBase) entity);
            }

            amount -= prevented;
            if (vitalStrike)
            {
                amount *= 3;
                prevented *= 3;
            }

            if (damageAttribute == Attributes.HEAT_DAMAGE)
            {
                Attributes.BODY_TEMPERATURE.setCurrentAmount(entity, Attributes.BODY_TEMPERATURE.getCurrentAmount(entity) + amount);
            }
            else if (damageAttribute == Attributes.COLD_DAMAGE)
            {
                Attributes.BODY_TEMPERATURE.setCurrentAmount(entity, Attributes.BODY_TEMPERATURE.getCurrentAmount(entity) - amount);
            }
            else
            {
                Attributes.HEALTH.setCurrentAmount(entity, Attributes.HEALTH.getCurrentAmount(entity) - amount);
            }
        }

        //Remove active armor mods
        if (useArmor)
        {
            for (ItemStack armor : armorCoverage.pool.keySet())
            {
                if (!armor.hasTagCompound()) continue;
                NBTTagCompound compound = MCTools.getSubCompoundIfExists(armor.getTagCompound(), "tiamatitems", "generic");
                if (compound != null)
                {
                    for (String key : compound.getKeySet())
                    {
                        BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(key);
                        if (attribute != null) attribute.setBaseAmount(entity, attribute.getBaseAmount(entity) - compound.getDouble(key));
                    }
                }
            }
        }
    }
}
