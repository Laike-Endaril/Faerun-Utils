package com.fantasticsource.faerunutils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;
import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class CmdDie extends CommandBase
{
    @Override
    public String getName()
    {
        return "die";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(getRequiredPermissionLevel(), getName())) return MODID + ".cmd." + getName() + ".usage";

        return "commands.generic.permission";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (!(sender instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) sender;
        System.out.println(TextFormatting.LIGHT_PURPLE + player.getName() + " used /die in dimension " + player.dimension + " (name: " + player.world.getWorldInfo().getWorldName() + ", dimetype: " + player.world.provider.getDimensionType() + ") at position: " + player.posX + ", " + player.posY + ", " + player.posZ);
        player.onKillCommand();
    }
}
