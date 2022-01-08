package com.fantasticsource.faerunutils;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

@Config(modid = MODID)
public class FaerunConfig
{
    @Config.Name("Patreon Names")
    @Config.Comment(
            {
                    "Syntax is...",
                    "pledgeCentsRequired, alteredName",
                    "alteredName is a string to display as their new name.  Use @p to enter their normal name anywhere in it",
                    "Eg if you wanted $5+ pledge patrons to have a dark green name...",
                    "500, §2@p"
            })
    public static String[] patreonNames = new String[0];
}
