package com.fantasticsource.faerunutils.professions;

import com.fantasticsource.faerunutils.professions.interactions.InteractionForgetRecipe;
import com.fantasticsource.faerunutils.professions.interactions.InteractionLearnProfession;
import com.fantasticsource.faerunutils.professions.interactions.InteractionQuitProfession;
import com.fantasticsource.faerunutils.professions.interactions.InteractionStartCrafting;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericString;

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

        String profession;
        boolean found;
        for (CItemType itemType : CSettings.LOCAL_SETTINGS.itemTypes.values())
        {
            profession = null;
            found = false;
            for (CRecalculableTrait trait : itemType.staticRecalculableTraits.values())
            {
                for (CRecalculableTraitElement element : trait.elements)
                {
                    if (element instanceof CRTraitElement_GenericString)
                    {
                        if (((CRTraitElement_GenericString) element).name.equals("mat1"))
                        {
                            found = true;
                            if (profession != null)
                            {
                                new InteractionForgetRecipe(profession, itemType.name);
                                break;
                            }
                        }
                        else if (((CRTraitElement_GenericString) element).name.equals("profession"))
                        {
                            profession = ((CRTraitElement_GenericString) element).value;
                            if (found)
                            {
                                new InteractionForgetRecipe(profession, itemType.name);
                                break;
                            }
                        }
                    }
                }

                if (found) break;
            }
        }
    }
}
