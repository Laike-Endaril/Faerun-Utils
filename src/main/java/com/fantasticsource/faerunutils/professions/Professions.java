package com.fantasticsource.faerunutils.professions;

import com.fantasticsource.faerunutils.professions.interactions.InteractionForgetProfession;
import com.fantasticsource.faerunutils.professions.interactions.InteractionLearnProfession;

public class Professions
{
    public static final String[] PROFESSIONS = new String[]
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

    public static final String[] PROFESSION_NPCS = new String[]
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

    public static final String LABOTOMIST_NAME = "Old Man Jace";

    public static void init()
    {
        for (String profession : PROFESSIONS)
        {
            new InteractionLearnProfession(profession, "crafting");
            new InteractionForgetProfession(profession);
        }
    }
}
