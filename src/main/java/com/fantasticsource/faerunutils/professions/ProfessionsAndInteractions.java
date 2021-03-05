package com.fantasticsource.faerunutils.professions;

import com.fantasticsource.faerunutils.professions.interactions.*;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_GenericString;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.faerunutils.FaerunUtils.MODID;

public class ProfessionsAndInteractions
{
    public static final String
            MASTER_SMITH = "Watts",
            MASTER_DESIGNER = null,
            INSURANCE_AGENT = "Pavel Roman Neko";

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
        new InteractionTemper(true);
        new InteractionTemper(false);

        new InteractionInsure("Entire Inventory");
        new InteractionInsure("All Equipped Items");
        new InteractionInsure("Mainhand");
        new InteractionInsure("Offhand");
        new InteractionInsure("Helm");
        new InteractionInsure("Shoulder Armor");
        new InteractionInsure("Cape");
        new InteractionInsure("Chestpiece");
        new InteractionInsure("Leg Armor");
        new InteractionInsure("Boots");

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


    public static int getExpReq(int level)
    {
        return 2000 + 200 * level * level;
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        for (ItemStack stack : event.newInventory.allNonSkin)
        {
            if (!Slottings.getItemSlotting(stack).equals("Tiamat Recipe")) continue;

            int level = MiscTags.getItemLevel(stack);
            if (level >= 5) continue;

            NBTTagCompound compound = stack.getTagCompound(), compound2 = MCTools.getOrGenerateSubCompound(compound, MODID);
            if (compound2.hasKey("exp")) continue;


            int req = getExpReq(level);
            compound2.setInteger("exp", 0);
            compound2.setInteger("expReq", req);

            if (AssemblyTags.hasInternalCore(stack))
            {
                compound2 = MCTools.getOrGenerateSubCompound(compound, "tiamatrpg", "core", "tag", MODID);
                compound2.setInteger("exp", 0);
                compound2.setInteger("expReq", req);
            }
        }
    }
}