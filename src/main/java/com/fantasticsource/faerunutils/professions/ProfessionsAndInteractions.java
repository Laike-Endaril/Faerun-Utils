package com.fantasticsource.faerunutils.professions;

import com.fantasticsource.faerunutils.professions.interactions.InteractionCreatePalette;
import com.fantasticsource.faerunutils.professions.interactions.InteractionInsure;

public class ProfessionsAndInteractions
{
    public static final String
            MASTER_DESIGNER = "Mabel Sable",
            INSURANCE_AGENT = "Pavel Roman Neko";

    public static void init()
    {
        new InteractionCreatePalette("Mainhand");
        new InteractionCreatePalette("Offhand");
        new InteractionCreatePalette("Sheathed Mainhand 1");
        new InteractionCreatePalette("Sheathed Offhand 1");
        new InteractionCreatePalette("Sheathed Mainhand 2");
        new InteractionCreatePalette("Sheathed Offhand 2");
        new InteractionCreatePalette("Headpiece");
        new InteractionCreatePalette("Shoulder Armor");
        new InteractionCreatePalette("Cape");
        new InteractionCreatePalette("Chestpiece");
        new InteractionCreatePalette("Leg Armor");
        new InteractionCreatePalette("Boots");

        new InteractionInsure("Entire Inventory");
        new InteractionInsure("All Equipped Items");
        new InteractionInsure("Mainhand");
        new InteractionInsure("Offhand");
        new InteractionInsure("Headpiece");
        new InteractionInsure("Shoulder Armor");
        new InteractionInsure("Cape");
        new InteractionInsure("Chestpiece");
        new InteractionInsure("Leg Armor");
        new InteractionInsure("Boots");
    }


    public static String getCostString(int cost)
    {
        int g = cost / 10000;
        int s = (cost / 100) % 100;
        int c = cost % 100;

        String result = "";
        if (g > 0) result += g + "g";
        if (s > 0) result += (g > 0 ? " " : "") + s + "s";
        result += (g > 0 || s > 0 ? " " : "") + c + "c";
        return result;
    }
}
