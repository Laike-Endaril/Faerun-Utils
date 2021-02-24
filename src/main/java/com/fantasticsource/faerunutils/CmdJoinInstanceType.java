package com.fantasticsource.faerunutils;

import com.fantasticsource.instances.InstanceData;
import com.fantasticsource.instances.server.Teleport;
import com.fantasticsource.instances.world.InstanceHandler;
import com.fantasticsource.instances.world.InstanceWorldProvider;
import com.fantasticsource.instances.world.dimensions.InstanceTypes;
import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.tools.Tools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;

import javax.annotation.Nullable;
import java.util.*;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class CmdJoinInstanceType extends CommandBase
{
    protected static final String[] HOSTILE_FACTIONS = new String[]{"Aggressive"};
    protected static final HashMap<String, ArrayList<FaerunInstanceData>> TEMP_INSTANCE_LISTS = new HashMap<>();

    @Override
    public String getName()
    {
        return "joinInstanceType";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(getRequiredPermissionLevel(), getName())) return MODID + ".cmd." + getName() + ".usage";

        return "commands.generic.permission";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        server.profiler.startSection("joinInstanceType");
        if (args.length < 7)
        {
            notifyCommandListener(sender, this, getUsage(sender));
            server.profiler.endSection();
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) PlayerData.getEntity(args[0]);
        if (player == null)
        {
            notifyCommandListener(sender, this, getUsage(sender));
            server.profiler.endSection();
            return;
        }

        String templateName = args[1];
        ArrayList<FaerunInstanceData> instanceDatasets = TEMP_INSTANCE_LISTS.computeIfAbsent(templateName, o -> new ArrayList<>());

        UUID id = player.getPersistentID();
        int maxPlayers = Integer.parseInt(args[2]), minHostiles = Integer.parseInt(args[3]), minDist = Integer.parseInt(args[4]);
        ArrayList<Vec3d> possiblePositions = new ArrayList<>();
        for (int i = 5; i + 2 < args.length; i += 3)
        {
            possiblePositions.add(new Vec3d(Integer.parseInt(args[i]) + 0.5, Integer.parseInt(args[i + 1]), Integer.parseInt(args[i + 2]) + 0.5));
        }

        instanceDatasets.removeIf(data2 -> data2.instance.playerEntities.size() == 0);
        instanceDatasets.sort(Comparator.comparingInt(data2 -> data2.instance.playerEntities.size()));

        FaerunInstanceData target = null;
        Vec3d targetPos = null;
        for (FaerunInstanceData data : instanceDatasets)
        {
            target = data;
            targetPos = data.getSpawnPos(id, maxPlayers, minHostiles, minDist, possiblePositions);

            if (targetPos != null) break;
        }

        if (targetPos != null)
        {
            target.blacklist.add(id);
            Teleport.joinPossiblyCreating(player, InstanceData.get(target.instance).getFullName(), targetPos);
        }
        else
        {
            targetPos = Tools.choose(possiblePositions);
            server.commandManager.executeCommand(server, "instances joinTempCopy Saved\\Template\\" + templateName + " " + player.getName() + " " + targetPos.x + " " + targetPos.y + " " + targetPos.z);
            if (player.world.provider instanceof InstanceWorldProvider)
            {
                FaerunInstanceData data = new FaerunInstanceData((WorldServer) player.world);
                data.blacklist.add(id);
                instanceDatasets.add(data);
            }
        }

        server.profiler.endSection();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        if (args.length == 2) return getListOfStringsMatchingLastWord(args, InstanceHandler.instanceFolderNames(true, InstanceTypes.TEMPLATE, false));
        return new ArrayList<>();
    }


    public static class FaerunInstanceData
    {
        public WorldServer instance;
        public ArrayList<UUID> blacklist = new ArrayList<>();

        public FaerunInstanceData(WorldServer instance)
        {
            this.instance = instance;
        }

        public Vec3d getSpawnPos(UUID playerID, int maxPlayers, int minHostiles, int minDist, ArrayList<Vec3d> possiblePositions)
        {
            if (blacklist.contains(playerID)) return null;
            if (instance.playerEntities.size() >= maxPlayers) return null;

            int hostiles = 0, minDistSqr = minDist * minDist;
            possiblePositions = new ArrayList<>(possiblePositions);
            for (Entity entity : instance.loadedEntityList)
            {
                Vec3d hostilePos = entity.getPositionVector();
                if (entity instanceof EntityPlayerMP)
                {
                    possiblePositions.removeIf(position -> position.squareDistanceTo(hostilePos) <= minDistSqr);
                }
                else
                {
                    IEntity iEntity = NpcAPI.Instance().getIEntity(entity);
                    if (iEntity instanceof ICustomNpc)
                    {
                        ICustomNpc npc = (ICustomNpc) iEntity;
                        if (Tools.contains(HOSTILE_FACTIONS, npc.getFaction().getName()))
                        {
                            hostiles++;
                            possiblePositions.removeIf(position -> position.squareDistanceTo(hostilePos) <= minDistSqr);
                        }
                    }
                }
            }

            if (hostiles < minHostiles) return null;
            return possiblePositions.size() == 0 ? null : Tools.choose(possiblePositions);
        }
    }
}
