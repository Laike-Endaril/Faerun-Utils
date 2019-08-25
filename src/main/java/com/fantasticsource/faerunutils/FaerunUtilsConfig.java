package com.fantasticsource.faerunutils;

import net.minecraftforge.common.config.Config;

@Config(modid = FaerunUtils.MODID)
public class FaerunUtilsConfig
{
    @Config.Name("010 GC Message Mode")
    @Config.Comment("0 = none, 1 = basic, 2 = detail")
    @Config.RangeInt(min = 0, max = 2)
    public static int gcMessageMode = 2;
}
