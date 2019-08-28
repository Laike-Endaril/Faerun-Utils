package com.fantasticsource.faerunutils;

import net.minecraftforge.common.config.Config;

@Config(modid = FaerunUtils.MODID)
public class FaerunUtilsConfig
{
    @Config.Name("010 GC Message Mode")
    @Config.Comment("0 = none, 1 = basic, 2 = detail")
    @Config.LangKey(FaerunUtils.MODID + ".config.gcMessageMode")
    @Config.RangeInt(min = 0, max = 2)
    public static int gcMessageMode = 2;

    @Config.Name("020 First Time Spawn Point")
    @Config.Comment("Where a player spawns the very first time they join the server; leave blank to disable.  Syntax is just comma-separated x, y, z")
    @Config.LangKey(FaerunUtils.MODID + ".config.firstTimeSpawn")
    public static String firstTimeSpawn = "";
}
