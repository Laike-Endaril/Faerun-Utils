package com.fantasticsource.faerunutils.ai;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.faerunutils.FaerunUtils;
import com.fantasticsource.faerunutils.actions.CFaerunAction;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Jab;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Kick;
import com.fantasticsource.faerunutils.actions.weapon.unarmed.Straight;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.tiamatactions.action.ActionQueue;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class AIFaerunMelee extends EntityAIAttackMelee
{
    public Path path2 = null;

    public AIFaerunMelee(EntityCreature creature)
    {
        super(creature, 0, false);
    }

    @Override
    public boolean shouldExecute()
    {
        return true;
    }

    @Override
    public void startExecuting()
    {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return;

        ArrayList<ActionData> possibleActions = getPossibleActions();
        possibleActions.sort((o1, o2) -> (int) (o2.dps - o1.dps));
        ActionData nextAction = null;
        ArrayList<CAction> actions = ActionQueue.get(attacker, "Main").queue;
        if (actions.size() > 0 && actions.get(0) instanceof CFaerunAction)
        {
            //TODO path for current action
        }
        double distance = attacker.getPositionVector().distanceTo(target.getPositionVector()), optimalDistance, rangeRange;
        for (ActionData data : possibleActions)
        {
            rangeRange = data.maxRange - data.minRange;
            optimalDistance = (data.maxRange + data.minRange) * 0.5;
            if (distance > optimalDistance + rangeRange * 0.25)
            {
                if (path2 == null || target.getPositionVector().squareDistanceTo(path2.getTarget().x, path2.getTarget().y, path2.getTarget().z) > 1)
                {
                    path2 = attacker.getNavigator().getPathToEntityLiving(target);
                    if (path2 != null) attacker.getNavigator().setPath(path2, 1);
                }
                if (path2 == null && distance > data.maxRange) continue;
            }
            else if (distance < optimalDistance - rangeRange * 0.25)
            {
                if (path2 == null || target.getPositionVector().squareDistanceTo(path2.getTarget().x, path2.getTarget().y, path2.getTarget().z) < distance)
                {
                    Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(attacker, 16, 7, target.getPositionVector());
                    if (vec3d == null) path2 = null;
                    else
                    {
                        path2 = attacker.getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
                        if (path2 != null) attacker.getNavigator().setPath(path2, 1);
                    }
                }
                if (path2 == null && distance < data.minRange) continue;
            }
            else path2 = null;

            nextAction = data;
            break;
        }

        if (nextAction != null)
        {

        }
        else
        {
            path2 = attacker.getNavigator().getPath();
            if (path2 == null || target.getPositionVector().squareDistanceTo(path2.getTarget().x, path2.getTarget().y, path2.getTarget().z) < distance)
            {
                Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(attacker, 16, 7, target.getPositionVector());
                if (vec3d == null) path2 = null;
                else
                {
                    path2 = attacker.getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
                    if (path2 != null) attacker.getNavigator().setPath(path2, 1);
                }
            }
        }


//        possibleActions.removeIf(actionData -> actionData.minRange > distance || actionData.maxRange < distance);


        ArrayList<Pair<Boolean, CFaerunAction>> actionPairs = getPossibleActions();
        for (Pair<Boolean, CFaerunAction> actionPair : actionPairs)
        {
            double minRange = 0, maxRange = 0;
            for (BetterAttributeMod mod : actionPair.getValue().attributeMods)
            {
                if (mod.betterAttributeName.equals(Attributes.MAX_MELEE_RANGE.name))
            }
        }
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    @Override
    public void updateTask()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        --this.delayCounter;

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F))
        {
            this.targetX = entitylivingbase.posX;
            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);

            if (this.canPenalize)
            {
                this.delayCounter += failedPathFindingPenalty;
                if (this.attacker.getNavigator().getPath() != null)
                {
                    net.minecraft.pathfinding.PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                        failedPathFindingPenalty = 0;
                    else
                        failedPathFindingPenalty += 10;
                }
                else
                {
                    failedPathFindingPenalty += 10;
                }
            }

            if (d0 > 1024.0D)
            {
                this.delayCounter += 10;
            }
            else if (d0 > 256.0D)
            {
                this.delayCounter += 5;
            }

            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget))
            {
                this.delayCounter += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    @Override
    public void resetTask()
    {
        attacker.getNavigator().clearPath();
        path2 = null;
    }

    protected ArrayList<ActionData> getPossibleActions()
    {
        ArrayList<ActionData> list = new ArrayList<>();
        list.add(new ActionData(new Kick(), true));

        ItemStack mainhand = attacker.getHeldItemMainhand(), offhand = attacker.getHeldItemOffhand();
        ArrayList<ItemStack> validItems = GlobalInventory.getValidEquippedItems(attacker);
        if (!validItems.contains(mainhand)) mainhand = ItemStack.EMPTY;
        if (!validItems.contains(offhand)) offhand = ItemStack.EMPTY;

        if (!mainhand.isEmpty() && offhand.isEmpty())
        {
            list.add(new ActionData(new Jab(), false));
            list.add(new ActionData(new Straight(), false));
        }
        else
        {
            list.add(new ActionData(new Jab(), true));
            list.add(new ActionData(new Straight(), true));
        }

        if (mainhand.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(mainhand.getTagCompound(), "tiamatitems", "generic");
            if (compound != null)
            {
                CAction action = CAction.ALL_ACTIONS.get(compound.getString(("mainhand0")));
                if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));
                action = CAction.ALL_ACTIONS.get(compound.getString(("mainhand1")));
                if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));

                if (Slottings.isTwoHanded(mainhand))
                {
                    action = CAction.ALL_ACTIONS.get(compound.getString(("offhand0")));
                    if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));
                    action = CAction.ALL_ACTIONS.get(compound.getString(("offhand1")));
                    if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));
                }
            }
        }

        if (offhand.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(offhand.getTagCompound(), "tiamatitems", "generic");
            if (compound != null)
            {
                CAction action = CAction.ALL_ACTIONS.get(compound.getString(("offhand0")));
                if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));
                action = CAction.ALL_ACTIONS.get(compound.getString(("offhand1")));
                if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));

                if (Slottings.isTwoHanded(offhand))
                {
                    action = CAction.ALL_ACTIONS.get(compound.getString(("mainhand0")));
                    if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));
                    action = CAction.ALL_ACTIONS.get(compound.getString(("mainhand1")));
                    if (action instanceof CFaerunAction) list.add(new ActionData((CFaerunAction) action, true));
                }
            }
        }

        list.removeIf(actionData -> !FaerunUtils.canUseAction(attacker, actionData.action));

        return list;
    }

    public static class ActionData
    {
        CFaerunAction action;
        double minRange, maxRange;
        double damage, dps;
        boolean mainhand;

        public ActionData(CFaerunAction action, boolean mainhand)
        {
            this.action = action;
            this.mainhand = mainhand;

            for (BetterAttributeMod mod : action.attributeMods)
            {
                if (mod.betterAttributeName.equals(Attributes.MIN_MELEE_RANGE.name)) minRange = mod.amount;
                if (mod.betterAttributeName.equals(Attributes.MAX_MELEE_RANGE.name)) maxRange = mod.amount;
                if (mod.betterAttributeName.equals(Attributes.SLASH_DAMAGE.name) || mod.betterAttributeName.equals(Attributes.PIERCE_DAMAGE.name) || mod.betterAttributeName.equals(Attributes.BLUNT_DAMAGE.name)) damage += mod.amount;
            }
            dps = damage / action.useTime;
        }
    }
}
