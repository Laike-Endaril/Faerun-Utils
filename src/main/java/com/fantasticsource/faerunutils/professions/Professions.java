package com.fantasticsource.faerunutils.professions;

import com.fantasticsource.faerunutils.professions.interactions.InteractionLearnProfession;
import com.fantasticsource.faerunutils.professions.interactions.InteractionQuitProfession;
import com.fantasticsource.faerunutils.professions.interactions.InteractionStartCrafting;

public class Professions
{
    public static final String[] CRAFTING_PROFESSIONS = new String[]
            {
                    "Weaponsmith",
                    "Armorsmith",
                    "Tailor",
                    "Leatherworker",
                    "Carpenter",
                    "Designer",
                    "Cook",
                    "Alchemist",
                    "Soul Crafter",
                    "Survivalist"
            };

    public static final String[] CRAFTING_PROFESSION_NPCS = new String[]
            {
                    "Colnas Meroh",
                    "Kate Fraser",
                    "Solomon Davy",
                    "Alice Larcher",
                    "Lixiss Daevaris",
                    "Darion Vosjan",
                    "Zelk",
                    "Callirhoe",
                    "Egon Spengler",
                    "Whisper Dreamwalker"
            };

    public static void init()
    {
        for (String profession : CRAFTING_PROFESSIONS)
        {
            new InteractionLearnProfession(profession, "crafting");
            new InteractionStartCrafting(profession);
            new InteractionQuitProfession(profession, "crafting");
        }
    }
}
