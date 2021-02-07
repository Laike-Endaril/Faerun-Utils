package com.fantasticsource.faerunutils.professions.crafting;

import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.gui.screen.ItemstackSelectionGUI;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;

public class CraftingGUI extends GUIScreen
{
    public final ItemStack professionItem;
    public final String profession;
    GUIText traitLabel, traitElement;
    GUIItemStack recipeElement, resultElement;
    protected boolean loadingOptions = false;
    protected ArrayList<String> traitOptions = new ArrayList<>();
    GUIAutocroppedView materialView;

    public CraftingGUI(ItemStack professionItem)
    {
        this.professionItem = professionItem;
        profession = TextFormatting.getTextWithoutFormattingCodes(professionItem.getDisplayName());

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(Minecraft.getMinecraft().player);
        if (inventory != null)
        {
            showUnstacked();

            root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

            GUIAutocroppedView mainView = new GUIAutocroppedView(this, 0.1);
            mainView.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
            root.add(mainView);

            mainView.background = new GUIDarkenedBackground(this);
            mainView.add(mainView.background);


            //Recipe
            GUIView recipeView = new GUIAutocroppedView(this);
            recipeView.setSubElementAutoplaceMethod(GUIElement.AP_Y_0_LEFT_TO_RIGHT);
            mainView.add(recipeView);

            GUIView subView = new GUIAutocroppedView(this);
            subView.setSubElementAutoplaceMethod(GUIElement.AP_X_0_TOP_TO_BOTTOM);
            recipeView.add(subView);

            GUIText recipeLabel = new GUIText(this, "Recipe:").setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            subView.addAll(new GUITextSpacer(this, 0, 0.5), recipeLabel);
            ArrayList<ItemStack> recipes = inventory.getCraftingRecipes();
            recipes.removeIf(recipe ->
            {
                if (recipe.isEmpty() || !recipe.hasTagCompound()) return true;
                return !profession.equals(recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString("profession"));
            });
            ItemStack recipe = recipes.size() > 0 ? recipes.get(0) : ItemStack.EMPTY;
            recipeElement = new GUIItemStack(this, 16, 16, recipe);
            GUIGradientBorder recipeBorder = new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK);
            recipeElement.add(recipeBorder);
            recipeLabel.linkMouseActivity(recipeBorder);
            recipeBorder.linkMouseActivity(recipeLabel);
            recipeLabel.addClickActions(recipeElement::click);
            recipeElement.addClickActions(() -> new ItemstackSelectionGUI(recipeElement, "Choose Recipe", recipes.toArray(new ItemStack[0])).addOnClosedActions(() ->
            {
                loadingOptions = true;
                Network.WRAPPER.sendToServer(new Network.RequestCraftOptionsPacket(recipe));
            }));
            recipeView.addAll(recipeElement);


            //Target Trait
            mainView.add(new GUITextSpacer(this, 0));

            GUIView traitView = new GUIAutocroppedView(this);
            traitView.setSubElementAutoplaceMethod(GUIElement.AP_Y_0_LEFT_TO_RIGHT);
            mainView.add(traitView);

            traitLabel = new GUIText(this, "").setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            traitElement = new GUIText(this, "").setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            traitLabel.linkMouseActivity(traitElement);
            traitElement.linkMouseActivity(traitLabel);
            traitLabel.addClickActions(traitElement::click);
            traitElement.addClickActions(() ->
            {
                if (!loadingOptions) new TextSelectionGUI(traitElement, "Select Target Trait", traitOptions.toArray(new String[0]));
            });
            traitView.addAll(traitLabel, traitElement);


            //Materials
            mainView.add(new GUITextSpacer(this, 0));
            mainView.add(new GUIText(this, "Materials..."));

            materialView = new GUIAutocroppedView(this);
            materialView.setSubElementAutoplaceMethod(GUIElement.AP_Y_0_LEFT_TO_RIGHT);
            mainView.add(materialView);


            //Craft button
            mainView.add(new GUITextSpacer(this, 0));
            mainView.add(new GUITextButton(this, "Craft").addClickActions(() ->
            {
                ItemStack[] mats = new ItemStack[materialView.children.size()];
                int i = 0;
                for (GUIElement element : materialView.children)
                {
                    mats[i] = ((GUIItemStack) element).getItemStack();
                    if (mats[i++].isEmpty()) return;
                }
                Network.WRAPPER.sendToServer(new Network.CraftPacket(profession, recipe, traitElement.getText(), mats));
            }));


            //Previous result
            mainView.add(new GUITextSpacer(this, 0));

            GUIView resultView = new GUIAutocroppedView(this);
            resultView.setSubElementAutoplaceMethod(GUIElement.AP_Y_0_LEFT_TO_RIGHT);
            mainView.add(resultView);

            subView = new GUIAutocroppedView(this);
            subView.setSubElementAutoplaceMethod(GUIElement.AP_X_0_TOP_TO_BOTTOM);
            resultView.add(subView);

            GUIText resultLabel = new GUIText(this, "Previous Result:");
            subView.addAll(new GUITextSpacer(this, 0, 0.5), resultLabel);
            resultElement = new GUIItemStack(this, 16, 16, ItemStack.EMPTY);
            GUIGradientBorder resultBorder = new GUIGradientBorder(this, 1, 1, 0.1, Color.WHITE, Color.BLANK);
            resultElement.add(resultBorder);
            resultView.addAll(resultElement);


            //Recalc for immediate render fix, then request options
            recalc();
            Network.WRAPPER.sendToServer(new Network.RequestCraftOptionsPacket(recipe));
        }
    }

    public void updateOptions(ArrayList<String> options)
    {
        //Trait options
        traitOptions.clear();
        for (String option : options) traitOptions.add(I18n.translateToLocal(option));
        if (options.size() == 0)
        {
            traitLabel.setText("");
            traitElement.setText("");
        }
        else
        {
            traitLabel.setText("Target Trait: ");
            if (!traitOptions.contains(traitElement.getText()))
            {
                traitElement.setText(traitOptions.get(0));
            }
        }


        //Materials
        materialView.clear();
        ItemStack recipe = recipeElement.getItemStack();
        if (recipe.hasTagCompound())
        {
            int i = 1;
            String key = "mat" + i;
            String value = recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString(key);
            while (!value.equals(""))
            {
                GUIItemStack materialElement = new GUIItemStack(this, 16, 16, ItemStack.EMPTY);
                materialView.add(materialElement);
                materialElement.add(new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK));
                String type = value;
                materialElement.addClickActions(() -> new ItemstackSelectionGUI(materialElement, "Choose Material", getValidMats(materialElement, type)));


                key = "mat" + ++i;
                value = recipe.getTagCompound().getCompoundTag("tiamatitems").getCompoundTag("generic").getString(key);
            }
        }


        //Recalc and finish
        recalc();
        loadingOptions = false;
    }

    protected ItemStack[] getValidMats(GUIItemStack currentElement, String type)
    {
        ArrayList<ItemStack> validMats = GlobalInventory.getAllNonSkinItems(Minecraft.getMinecraft().player);
        ArrayList<ItemStack> otherElementMats = new ArrayList<>();
        for (GUIElement element : currentElement.parent.children)
        {
            if (element == currentElement) continue;
            if (!(element instanceof GUIItemStack)) continue;
            ItemStack stack = ((GUIItemStack) element).getItemStack();
            if (!stack.isEmpty()) otherElementMats.add(stack);
        }
        validMats.removeIf(material -> otherElementMats.contains(material) || !type.equals(MiscTags.getItemTypeName(material)));

        return validMats.toArray(new ItemStack[0]);
    }

    public void setPreviousResult(ItemStack result)
    {
        resultElement.setItemStack(result);
    }

    @Override
    public String title()
    {
        return "Crafting (" + profession + ")";
    }
}
