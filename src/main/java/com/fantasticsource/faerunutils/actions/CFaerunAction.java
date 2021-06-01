package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.faeruncharacters.VoiceSets;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.actions.weapon.axe.Chop;
import com.fantasticsource.faerunutils.actions.weapon.axe.OverheadChop;
import com.fantasticsource.faerunutils.actions.weapon.bow.Shot;
import com.fantasticsource.faerunutils.actions.weapon.mace.Bash;
import com.fantasticsource.faerunutils.actions.weapon.mace.OverheadBash;
import com.fantasticsource.faerunutils.actions.weapon.quarterstaff.LonghandStrike;
import com.fantasticsource.faerunutils.actions.weapon.quarterstaff.SpinningStrikes;
import com.fantasticsource.faerunutils.actions.weapon.sickle.StabbingSwing;
import com.fantasticsource.faerunutils.actions.weapon.sickle.TrippingSlash;
import com.fantasticsource.faerunutils.actions.weapon.spear.LongThrust;
import com.fantasticsource.faerunutils.actions.weapon.sword.Slash;
import com.fantasticsource.faerunutils.actions.weapon.sword.Thrust;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Jab;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Kick;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Straight;
import com.fantasticsource.faerunutils.animations.CFaerunAnimation;
import com.fantasticsource.mctools.EntityFilters;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatactions.config.TiamatActionsConfig;
import com.fantasticsource.tiamatactions.node.CNode;
import com.fantasticsource.tiamatactions.node.CNodeComment;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.DecimalWeightedPool;
import com.fantasticsource.tools.datastructures.Pair;
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
    public static final double DODGE_COST = 5;
    public static final HashMap<Entity, ArrayList<Pair<Boolean, CFaerunAnimation>>> USED_COMBO_ANIMATIONS = new HashMap<>();
    public static final Field ENTITY_LIVING_BASE_LAST_DAMAGE_FIELD = ReflectionTool.getField(EntityLivingBase.class, "field_110153_bc", "lastDamage");

    public double useTime = 0, percentTimeBeforeHit = 1, hpCost = 0, mpCost = 0, staminaCost = 0, comboUsage = 0, timer = 0, progressPerSecond = 1, progressPerTick = 0.05;
    public ArrayList<BetterAttributeMod> attributeMods = new ArrayList<>();
    public ArrayList<String> categoryTags = new ArrayList<>(), canComboTo = new ArrayList<>();
    public ItemStack itemstackUsed = null;
    public boolean mainhand = true, playedSwishSound = false, selfInterruptible = true, didTheThing = false;
    public String material;
    public Class<? extends CFaerunAnimation>[] animationsToUse = new Class[0];
    protected CFaerunAnimation animation = null;

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
                if (Attributes.HEALTH.getCurrentAmount(source) - hpCost < 0 || Attributes.MANA.getCurrentAmount(source) - mpCost < 0 || Attributes.STAMINA.getCurrentAmount(source) - staminaCost < 0)
                {
                    if (comboUsage > 0) Attributes.COMBO.setCurrentAmount(source, Attributes.COMBO.getCurrentAmount(source) + comboUsage); //Refund combo usage
                    if (!(this instanceof Cooldown) && queue.queue.size() == 1) new ComboGracePeriod(this, 0.5).queue(source, queue.name);
                    active = false;
                    return;
                }


                if (hpCost > 0)
                {
                    Attributes.HEALTH.setCurrentAmount(source, Attributes.HEALTH.getCurrentAmount(source) - hpCost);
                }
                if (mpCost > 0)
                {
                    Attributes.MANA.setCurrentAmount(source, Attributes.MANA.getCurrentAmount(source) - mpCost);
                }
                if (staminaCost > 0)
                {
                    Attributes.STAMINA.setCurrentAmount(source, Attributes.STAMINA.getCurrentAmount(source) - staminaCost);
                }

                BetterAttributeMod.addMods(source, attributeMods.toArray(new BetterAttributeMod[0]));

                progressPerSecond = Attributes.ATTACK_SPEED.getTotalAmount(source) * 0.01;
                progressPerTick = progressPerSecond * 0.05;
                if (getClass() != ComboGracePeriod.class)
                {
                    ArrayList<Pair<Boolean, CFaerunAnimation>> animations = USED_COMBO_ANIMATIONS.get(source);
                    if (animations != null)
                    {
                        for (Pair<Boolean, CFaerunAnimation> animation : animations) CBipedAnimation.removeAnimation(source, animation.getValue());
                    }
                }
                playAnimation();
                playExertionSound();

                for (CNode endNode : startEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                break;


            case "tick":
                for (CNode endNode : tickEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results);
                timer += progressPerTick;
                double hitTime = useTime * percentTimeBeforeHit;

                if (!playedSwishSound && (hitTime - timer) / progressPerSecond <= 0.15)
                {
                    playSwishSound();
                    playedSwishSound = true;
                }

                if (!didTheThing && timer >= hitTime)
                {
                    doStuff();
                    didTheThing = true;
                }

                if (timer >= useTime) active = false;
                break;


            case "end":
                for (CNode endNode : endEndpointNodes.toArray(new CNode[0])) endNode.executeTree(mainAction, this, results, true);
                BetterAttributeMod.removeModsWithNameContaining(source, name, true);

                if (!(this instanceof Cooldown) && queue.queue.size() == 1) new ComboGracePeriod(this, 0.5).queue(source, queue.name);
                if (!MCTools.entityIsValid(source)) USED_COMBO_ANIMATIONS.remove(source); //Remove data if the action ended due to the entity becoming invalid
                break;
        }

        if (profile) profiler.endSection();
        if (profile) profiler.endSection();
    }


    protected void playAnimation()
    {
        if (animationsToUse.length == 0) return;


        Class<? extends CFaerunAnimation> animationToUse = animationsToUse[0];

        ArrayList<Pair<Boolean, CFaerunAnimation>> previousAnimations = USED_COMBO_ANIMATIONS.get(source);
        if (previousAnimations != null)
        {
            Pair<Boolean, CFaerunAnimation> previousAnimation = previousAnimations.get(previousAnimations.size() - 1);
            int index = Tools.indexOf(animationsToUse, previousAnimation.getValue().getClass()) + 1;
            if (previousAnimation.getKey() != mainhand || index >= animationsToUse.length) index = 0;
            animationToUse = animationsToUse[index];
        }


        try
        {
            animation = animationToUse.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        animation.setAllRates(progressPerSecond * animation.hitTime / (useTime * percentTimeBeforeHit));
        animation.start(source, mainhand);
        USED_COMBO_ANIMATIONS.computeIfAbsent(source, o -> new ArrayList<>()).add(new Pair<>(mainhand, animation));
    }


    protected void doStuff()
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
                double stamina = Attributes.STAMINA.getCurrentAmount(entity);
                double blockCost = Attributes.BLUNT_DAMAGE.getTotalAmount(source) * 0.25 + Attributes.SLASH_DAMAGE.getTotalAmount(source) * 0.15 + Attributes.PIERCE_DAMAGE.getTotalAmount(source) * 0.1;
                if (stamina >= blockCost)
                {
                    //Block
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

                            Attributes.STAMINA.setCurrentAmount(entity, stamina - blockCost);

                            //TODO visual block indicators

                            return;
                        }
                    }

                    //Parry
                    chance = (Attributes.PARRY_CHANCE.getTotalAmount(entity) - finesse) / 100d;
                    if (thrust) chance *= 0.75;
                    if (Math.random() < chance)
                    {
                        ItemStack parryingStack = bestParryStack(((EntityLivingBase) entity).getHeldItemMainhand(), ((EntityLivingBase) entity).getHeldItemOffhand(), isHeavy(itemstackUsed));
                        if (!isHeavy(itemstackUsed) || canBlockHeavy(parryingStack))
                        {
                            if (material.equals("flesh") && !getParryMaterial(parryingStack).equals("flesh")) onHit(source);
                            else playParrySounds(entity, parryingStack);

                            Attributes.STAMINA.setCurrentAmount(entity, stamina - blockCost * 0.5);

                            //TODO visual parry indicators

                            finesse *= 0.5;
                            targets--;
                            continue;
                        }
                    }
                }

                //Dodge
                if (stamina >= DODGE_COST)
                {
                    chance = (Attributes.DODGE_CHANCE.getTotalAmount(entity) - finesse) / 100d;
                    if (thrust) chance *= 1.25;
                    if (Math.random() < chance)
                    {
                        Attributes.STAMINA.setCurrentAmount(entity, stamina - DODGE_COST);

                        //TODO visual dodge indicators
                        continue;
                    }
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
        double totalDamage = 0;
        for (BetterAttribute damageAttribute : BetterAttribute.BETTER_ATTRIBUTES.values())
        {
            BetterAttribute defenseAttribute = Attributes.getDefenseAttribute(damageAttribute);
            if (defenseAttribute == null) continue;

            double amount = damageAttribute.getTotalAmount(source);
            totalDamage += amount;
            if (amount <= 0) continue;

            double prevented = defenseAttribute.getTotalAmount(entity);

            //Armor damage
            if (useArmor && armorToDamage != null && prevented > 0)
            {
                double armorDamage = prevented;
                NBTTagCompound compound = MCTools.getSubCompoundIfExists(armorToDamage.getTagCompound(), MODID);
                if (compound != null) armorDamage -= compound.getDouble("armor." + damageAttribute.name + ".reduction");
                if (armorDamage > 0) armorToDamage.damageItem((int) armorDamage, (EntityLivingBase) entity);
            }

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

        if (totalDamage > 0) playHitSound();

        //Remove active armor mods
        BetterAttributeMod.removeMods(entity, activeArmorMods.toArray(new BetterAttributeMod[0]));
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


    public void playExertionSound()
    {
        if (this instanceof Cooldown) return;

        String voice = CharacterTags.getCC((EntityLivingBase) source).getString("Voice");
        ResourceLocation soundRL = VoiceSets.ALL_VOICE_SETS.get(voice).get("attack");

        MCTools.playSimpleSoundForAll(soundRL, source, 16, 2, 1, 0.8f + Tools.random(0.4f), SoundCategory.HOSTILE);
    }


    public void playSwishSound()
    {
        if (this instanceof Cooldown) return;

        String soundName = Attributes.MAX_MELEE_ANGLE.getTotalAmount(source) == 0 ? "thrust" : "swing";

        float pitch = 1;
        if (categoryTags.contains("Heavy")) pitch -= .4f;

        MCTools.playSimpleSoundForAll(new ResourceLocation(soundName), source, 16, 2, 1, pitch - 0.2f + Tools.random(0.4f), SoundCategory.HOSTILE);
    }


    public void playBlockSound(ItemStack blockingItemstack)
    {
        if (this instanceof Cooldown) return;

        String blockMat = getBlockMaterial(blockingItemstack);
        if (material.equals("flesh") && !blockMat.equals("flesh")) playHitSound();
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

    public void playHitSound()
    {
        if (this instanceof Cooldown) return;

        double slash = Attributes.SLASH_DAMAGE.getTotalAmount(source);
        double pierce = Attributes.PIERCE_DAMAGE.getTotalAmount(source);
        double blunt = Attributes.BLUNT_DAMAGE.getTotalAmount(source);
        String soundName = slash >= pierce && slash >= blunt ? "slash" : pierce >= blunt ? "pierce" : "bash";

        float pitch = 1;
        if (categoryTags.contains("Heavy")) pitch -= .4f;

        MCTools.playSimpleSoundForAll(new ResourceLocation(soundName), source, 16, 2, 1, pitch - 0.2f + Tools.random(0.4f), SoundCategory.HOSTILE);
    }


    public static void init(FMLPostInitializationEvent event)
    {
        //Unarmed
        new Jab().save();
        new Straight().save();
        new Kick().save();

        //Sword
        new Slash().save();
        new Thrust().save();

        //Axe
        new Chop().save();
        new OverheadChop().save();

        //Dagger
        new com.fantasticsource.faerunutils.actions.weapon.dagger.Slash().save();
        new com.fantasticsource.faerunutils.actions.weapon.dagger.Thrust().save();

        //Katar
        new com.fantasticsource.faerunutils.actions.weapon.katar.Thrust().save();
        new com.fantasticsource.faerunutils.actions.weapon.katar.Slash().save();

        //Mace
        new Bash().save();
        new OverheadBash().save();

        //Sickle
        new StabbingSwing().save();
        new TrippingSlash().save();


        //Shield
        new com.fantasticsource.faerunutils.actions.weapon.shield.Bash().save();


        //Spear
        new com.fantasticsource.faerunutils.actions.weapon.spear.Thrust().save();
        new LongThrust().save();

        //Quarterstaff
        new com.fantasticsource.faerunutils.actions.weapon.quarterstaff.Jab().save();
        new SpinningStrikes().save();
        new LonghandStrike().save();
        new com.fantasticsource.faerunutils.actions.weapon.quarterstaff.OverheadBash().save();


        //Polearm
        new com.fantasticsource.faerunutils.actions.weapon.polearm.Slash().save();
        new com.fantasticsource.faerunutils.actions.weapon.polearm.Thrust().save();
        new com.fantasticsource.faerunutils.actions.weapon.polearm.Bash().save();

        //Greatsword
        new com.fantasticsource.faerunutils.actions.weapon.greatsword.Slash().save();
        new com.fantasticsource.faerunutils.actions.weapon.greatsword.Thrust().save();

        //Greataxe
        new com.fantasticsource.faerunutils.actions.weapon.greataxe.Slash().save();
        new com.fantasticsource.faerunutils.actions.weapon.greataxe.OverheadChop().save();

        //Hammer
        new com.fantasticsource.faerunutils.actions.weapon.hammer.Bash().save();
        new com.fantasticsource.faerunutils.actions.weapon.hammer.OverheadBash().save();

        //Scythe
        new com.fantasticsource.faerunutils.actions.weapon.scythe.StabbingSwing().save();
        new com.fantasticsource.faerunutils.actions.weapon.scythe.TrippingSlash().save();


        //Bow
        new Shot().save();
    }
}
