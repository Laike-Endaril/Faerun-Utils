package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Jab;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Kick;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Straight;
import com.fantasticsource.mctools.EntityFilters;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatactions.config.TiamatActionsConfig;
import com.fantasticsource.tiamatactions.node.CNode;
import com.fantasticsource.tiamatactions.node.CNodeComment;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.DecimalWeightedPool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public abstract class CFaerunAction extends CAction
{
    public static final Field ENTITY_LIVING_BASE_LAST_DAMAGE_FIELD = ReflectionTool.getField(EntityLivingBase.class, "field_110153_bc", "lastDamage");

    public double useTime = 0, hpCost = 0, mpCost = 0, staminaCost = 0, comboUsage = 0, timer = 0;
    public ArrayList<BetterAttributeMod> attributeMods = new ArrayList<>();
    public ArrayList<String> categoryTags = new ArrayList<>(), canComboTo = new ArrayList<>();
    public ItemStack itemstackUsed = null;
    public boolean playedSwishSound = false;
    public String material;

    public CFaerunAction()
    {
        super();
        if (tickEndpointNodes.size() == 0)
        {
            CNodeComment node = new CNodeComment(name, "tick", 0, 0);
            tickNodes.put(0L, node);
            tickEndpointNodes.add(node, 0);
        }
    }

    public CFaerunAction(String name)
    {
        super(name);
        if (tickEndpointNodes.size() == 0)
        {
            CNodeComment node = new CNodeComment(name, "tick", 0, 0);
            tickNodes.put(0L, node);
            tickEndpointNodes.add(node, 0);
        }
    }

    public String getTooltip()
    {
        StringBuilder builder = new StringBuilder(name);

        if (comboUsage > 0) builder.append("\n" + TextFormatting.YELLOW + "Combo Usage: " + Tools.formatNicely(comboUsage));
        if (useTime > 0) builder.append("\n" + TextFormatting.YELLOW + "Use Time: " + Tools.formatNicely(useTime) + "s");
        if (hpCost > 0) builder.append("\n" + TextFormatting.RED + "HP Cost: " + Tools.formatNicely(hpCost));
        if (mpCost > 0) builder.append("\n" + TextFormatting.BLUE + "MP Cost: " + Tools.formatNicely(mpCost));
        if (staminaCost > 0) builder.append("\n" + TextFormatting.GOLD + "Stamina Cost: " + Tools.formatNicely(staminaCost));

        if (attributeMods.size() > 0)
        {
            builder.append("\n\nDuring this action...");
            for (BetterAttributeMod mod : attributeMods)
            {
                builder.append("\n" + mod);
            }
        }

        if (categoryTags.size() > 0)
        {
            builder.append("\n\nCategories:");
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
                if (comboUsage > 0) Attributes.COMBO.setCurrentAmount(source, Attributes.COMBO.getCurrentAmount(source) - comboUsage);
                for (CNode endNode : initEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                break;

            case "start":
                if (hpCost > 0) Attributes.HEALTH.setCurrentAmount(source, Attributes.HEALTH.getCurrentAmount(source) - hpCost);
                if (mpCost > 0) Attributes.MANA.setCurrentAmount(source, Attributes.MANA.getCurrentAmount(source) - mpCost);
                if (staminaCost > 0) Attributes.STAMINA.setCurrentAmount(source, Attributes.STAMINA.getCurrentAmount(source) - staminaCost);
                BetterAttributeMod.addMods(source, attributeMods.toArray(new BetterAttributeMod[0]));
                for (CNode endNode : startEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                break;

            case "tick":
                for (CNode endNode : tickEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                double tempo = Attributes.ATTACK_SPEED.getTotalAmount(source) * 0.01;
                double tickProgress = tempo * 0.05;
                timer += tickProgress;

                if (!playedSwishSound && (useTime - timer) / tempo <= 0.15) playSwishSound();

                if (timer >= useTime)
                {
                    onCompletion();
                    active = false;
                }
                break;

            case "end":
                for (CNode endNode : endEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results, true);
                BetterAttributeMod.removeModsWithNameContaining(source, name, true);
                if (!(this instanceof Cooldown) && queue.queue.size() == 1) new Cooldown(2).queue(source, queue.name);
                break;
        }

        if (profile) profiler.endSection();
        if (profile) profiler.endSection();
    }

    protected void onCompletion()
    {
        int targets = (int) Attributes.MAX_MELEE_TARGETS.getTotalAmount(source);
        if (targets == 0) return;


        ArrayList<Entity> entities = new ArrayList<>(source.world.loadedEntityList);
        entities.remove(source);
        double minRange = Attributes.MIN_MELEE_RANGE.getTotalAmount(source);
        Vec3d sourceEyes = source.getPositionVector().addVector(0, source.getEyeHeight(), 0);
        if (minRange > 0)
        {
            double minSqr = minRange * minRange;
            entities.removeIf(entity ->
            {
                if (!entity.isEntityAlive() || !(entity instanceof EntityLivingBase)) return true;
                return entity.getPositionVector().addVector(0, entity.height * 0.5, 0).squareDistanceTo(sourceEyes) > minSqr;
            });
        }

        double finesse = Attributes.FINESSE.getTotalAmount(source);
        boolean thrust = Attributes.MAX_MELEE_ANGLE.getTotalAmount(source) == 0;
        double chance;
        for (Entity entity : EntityFilters.inCone(sourceEyes, source.getRotationYawHead(), source.rotationPitch, source.width * 0.5 + Attributes.MAX_MELEE_RANGE.getTotalAmount(source), Attributes.MAX_MELEE_ANGLE.getTotalAmount(source), true, entities))
        {
            if (Sight.canSee((EntityLivingBase) entity, source, true))
            {
                chance = Attributes.BLOCK_CHANCE.getTotalAmount(entity) / 100d;
                if (thrust) chance *= 0.75;
                if (FaerunUtils.canBlock(entity) && Math.random() < chance)
                {
                    ItemStack blockingStack = bestBlockStack(((EntityLivingBase) entity).getHeldItemMainhand(), ((EntityLivingBase) entity).getHeldItemOffhand(), isHeavy(itemstackUsed));
                    if (!isHeavy(itemstackUsed) || canBlockHeavy(blockingStack))
                    {
                        String blockMat = getBlockMaterial(blockingStack);
                        if (material.equals("flesh") && !blockMat.equals("flesh")) onHit(source);
                        else if (blockMat.equals("flesh") && !material.equals("flesh")) onHit(entity);
                        else playBlockSound(blockingStack);

                        //TODO visual block indicators

                        return;
                    }
                }

                chance = (Attributes.PARRY_CHANCE.getTotalAmount(entity) - finesse) / 100d;
                if (thrust) chance *= 0.75;
                if (Math.random() < chance)
                {
                    ItemStack parryingStack = bestParryStack(((EntityLivingBase) entity).getHeldItemMainhand(), ((EntityLivingBase) entity).getHeldItemOffhand(), isHeavy(itemstackUsed));
                    if (!isHeavy(itemstackUsed) || canBlockHeavy(parryingStack))
                    {
                        if (material.equals("flesh") && !getParryMaterial(parryingStack).equals("flesh")) onHit(source);
                        else playParrySounds(entity, parryingStack);

                        //TODO visual parry indicators

                        finesse *= 0.5;
                        targets--;
                        continue;
                    }
                }

                chance = (Attributes.DODGE_CHANCE.getTotalAmount(entity) - finesse) / 100d;
                if (thrust) chance *= 1.25;
                if (Math.random() < chance)
                {
                    //TODO visual dodge indicators
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
        boolean vitalStrike = Math.random() < Attributes.VITAL_STRIKE_CHANCE.getTotalAmount(source) * 0.01;

        //Apply active armor mods
        ArrayList<BetterAttributeMod> activeArmorMods = new ArrayList<>();
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

                NBTTagCompound compound = MCTools.getSubCompoundIfExists(armor.getTagCompound(), MODID, "activeArmorMods");
                if (compound != null)
                {
                    for (String key : compound.getKeySet())
                    {
                        BetterAttributeMod mod = new BetterAttributeMod();
                        mod.deserializeNBT(compound.getCompoundTag(key));
                        activeArmorMods.add(mod);
                    }
                }
            }

            BetterAttributeMod.addMods(entity, activeArmorMods.toArray(new BetterAttributeMod[0]));
        }

        //Deal damage
        ItemStack armorToDamage = armorCoverage.getRandom();
        for (BetterAttribute damageAttribute : BetterAttribute.BETTER_ATTRIBUTES.values())
        {
            BetterAttribute defenseAttribute = Attributes.getDefenseAttribute(damageAttribute);
            if (defenseAttribute == null) continue;

            double amount = damageAttribute.getTotalAmount(source);
            if (amount <= 0) continue;

            double prevented = defenseAttribute.getTotalAmount(entity);

            //Armor damage
            if (useArmor && armorToDamage != null && prevented > 0)
            {
                double armorDamage = prevented;
                NBTTagCompound compound = MCTools.getSubCompoundIfExists(armorToDamage.getTagCompound(), MODID);
                if (compound != null) armorDamage -= compound.getDouble("armor." + damageAttribute.name + ".reduction");
                if (armorDamage > 0) armorToDamage.damageItem((int) armorDamage, (EntityLivingBase) entity);
                playHitSound(true);
            }
            else playHitSound(false);

            //Vital strike
            amount -= prevented;
            if (vitalStrike)
            {
                amount *= 3;
                prevented *= 3;
            }

            //Body damage
            if (damageAttribute == Attributes.HEAT_DAMAGE)
            {
                Attributes.BODY_TEMPERATURE.setCurrentAmount(entity, Attributes.BODY_TEMPERATURE.getCurrentAmount(entity) + amount);
            }
            else if (damageAttribute == Attributes.COLD_DAMAGE)
            {
                Attributes.BODY_TEMPERATURE.setCurrentAmount(entity, Attributes.BODY_TEMPERATURE.getCurrentAmount(entity) - amount);
            }
            else if (damageAttribute == Attributes.HEALING_DAMAGE)
            {
                if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isEntityUndead())
                {
                    entity.attackEntityFrom(new EntityDamageSource(MODID, source), (float) amount);
                }
                else
                {
                    Attributes.HEALTH.setCurrentAmount(entity, Tools.min(Attributes.HEALTH.getTotalAmount(entity), Attributes.HEALTH.getCurrentAmount(entity) + amount));
                }
            }
            else
            {
                entity.attackEntityFrom(new EntityDamageSource(MODID, source).setDamageBypassesArmor().setDamageIsAbsolute(), (float) amount);
                ReflectionTool.set(ENTITY_LIVING_BASE_LAST_DAMAGE_FIELD, entity, 0);
            }
        }

        //Remove active armor mods
        BetterAttributeMod.removeMods(entity, activeArmorMods.toArray(new BetterAttributeMod[0]));
    }

    public static void init(FMLPostInitializationEvent event)
    {
        new Jab().save();
        new Straight().save();
        new Kick().save();
    }


    public static String getBlockMaterial(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "flesh";
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        if (compound == null || !compound.hasKey("blockMaterial")) return "flesh";
        return compound.getString("blockMaterial");
    }

    public static String getParryMaterial(ItemStack stack)
    {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return "flesh";
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        if (compound == null || !compound.hasKey("parryMaterial")) return "flesh";
        return compound.getString("parryMaterial");
    }

    public static boolean isHeavy(ItemStack stack)
    {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return false;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound != null && compound.getBoolean("heavy");
    }

    public static boolean canBlockHeavy(ItemStack stack)
    {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return false;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID);
        return compound != null && compound.getBoolean("canBlockHeavy");
    }


    public static ItemStack bestBlockStack(ItemStack stack1, ItemStack stack2, boolean requireHeavyBlocking)
    {
        //Extra checks if heavy blocking is required
        if (requireHeavyBlocking)
        {
            if (!canBlockHeavy(stack2))
            {
                if (!canBlockHeavy(stack1)) return ItemStack.EMPTY;
                return stack1;
            }
            else if (!canBlockHeavy(stack1)) return stack2;
        }

        //Prefer heavier
        boolean heavy1 = isHeavy(stack1), heavy2 = isHeavy(stack2);
        if (heavy1 != heavy2) return heavy1 ? stack1 : stack2;
        //Both heavy or both light

        //Prefer harder
        String mat1 = getBlockMaterial(stack1), mat2 = getBlockMaterial(stack2);
        if (!mat1.equals(mat2))
        {
            if (mat1.equals("metal")) return stack1;
            if (mat2.equals("metal")) return stack2;
            if (mat1.equals("wood")) return stack1;
            return stack2;
        }
        //Both same material

        return stack1;
    }

    public static ItemStack bestParryStack(ItemStack stack1, ItemStack stack2, boolean requireHeavyParrying)
    {
        //Extra checks if heavy blocking is required
        if (requireHeavyParrying)
        {
            if (!canBlockHeavy(stack2))
            {
                if (!canBlockHeavy(stack1)) return ItemStack.EMPTY;
                return stack1;
            }
            else if (!canBlockHeavy(stack1)) return stack2;
        }

        //Prefer harder
        String mat1 = getBlockMaterial(stack1), mat2 = getBlockMaterial(stack2);
        if (!mat1.equals(mat2))
        {
            if (mat1.equals("metal")) return stack1;
            if (mat2.equals("metal")) return stack2;
            if (mat1.equals("wood")) return stack1;
            return stack2;
        }
        //Both same material

        //Prefer lighter
        boolean heavy1 = isHeavy(stack1), heavy2 = isHeavy(stack2);
        if (heavy1 != heavy2) return heavy1 ? stack2 : stack1;
        //Both heavy or both light

        return stack1;
    }


    public void playSwishSound()
    {
        if (this instanceof Cooldown) return;

        playedSwishSound = true;
        String soundName = Attributes.MAX_MELEE_ANGLE.getTotalAmount(source) == 0 ? "thrust" : "swing";

        float pitch = 1;
        if (categoryTags.contains("Heavy")) pitch -= .4f;

        MCTools.playSimpleSoundForAll(new ResourceLocation(soundName), source, 16, 2, 1, pitch - 0.2f + Tools.random(0.4f), SoundCategory.HOSTILE);
    }

    public void playBlockSound(ItemStack blockingItemstack)
    {
        if (this instanceof Cooldown) return;

        String blockMat = getBlockMaterial(blockingItemstack);
        if (material.equals("flesh") && !blockMat.equals("flesh"))
        {
            playHitSound(true);
            //TODO damage self instead of enemy
        }
        else MCTools.playSimpleSoundForAll(new ResourceLocation((isHeavy(blockingItemstack) ? "heavy" : "light") + blockMat + "block" + (categoryTags.contains("Heavy") ? "heavy" : "light") + material), source, 16, 2, 1, 0.8f + Tools.random(0.4f), SoundCategory.HOSTILE);
    }

    public void playParrySounds(Entity parryingEntity, ItemStack parryingItemstack)
    {
        if (this instanceof Cooldown) return;

        String parryMat = getParryMaterial(parryingItemstack);
        if (parryMat.equals("flesh"))
        {
            MCTools.playSimpleSoundForAll(new ResourceLocation("fleshparry"), source, 16, 2, 1, 0.8f + Tools.random(0.4f), SoundCategory.HOSTILE);
        }
        else
        {
            MCTools.playSimpleSoundForAll(new ResourceLocation((categoryTags.contains("Heavy") ? "heavy" : "light") + material + "parry"), source, 16, 2, 1, 0.8f + Tools.random(0.4f), SoundCategory.HOSTILE);
            MCTools.playSimpleSoundForAll(new ResourceLocation((isHeavy(parryingItemstack) ? "heavy" : "light") + parryMat + "parry"), parryingEntity, 16, 2, 1, 0.8f + Tools.random(0.4f), SoundCategory.HOSTILE);
        }
    }

    public void playHitSound(boolean hitArmor)
    {
        if (this instanceof Cooldown) return;

        double slash = Attributes.SLASH_DAMAGE.getTotalAmount(source);
        double pierce = Attributes.PIERCE_DAMAGE.getTotalAmount(source);
        double blunt = Attributes.BLUNT_DAMAGE.getTotalAmount(source);
        String soundName = slash >= pierce && slash >= blunt ? "slash" : pierce >= blunt ? "pierce" : "bash";

        soundName += hitArmor ? "armor" : "body";

        float pitch = 1;
        if (categoryTags.contains("Heavy")) pitch -= .4f;

        MCTools.playSimpleSoundForAll(new ResourceLocation(soundName), source, 16, 2, 1, pitch - 0.2f + Tools.random(0.4f), SoundCategory.HOSTILE);
    }
}
