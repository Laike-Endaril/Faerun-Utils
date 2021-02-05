package com.fantasticsource.faerunutils.professions.crafting;

import com.fantasticsource.faerunutils.Network;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.gui.screen.ItemstackSelectionGUI;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
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
    GUIItemStack recipeElement;
    protected boolean loadingOptions = false;
    protected ArrayList<String> traitOptions = new ArrayList<>();

    public CraftingGUI(ItemStack professionItem)
    {
        this.professionItem = professionItem;
        profession = TextFormatting.getTextWithoutFormattingCodes(professionItem.getDisplayName());

        ITiamatPlayerInventory inventory = TiamatInventoryAPI.getTiamatPlayerInventory(Minecraft.getMinecraft().player);
        if (inventory != null)
        {
            showUnstacked();

            root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

            GUIAutocroppedView mainView = new GUIAutocroppedView(this, 0.2);
            mainView.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
            root.add(mainView);

            mainView.background = new GUIDarkenedBackground(this);
            mainView.add(mainView.background);


            //Recipe
            GUIView recipeView = new GUIAutocroppedView(this);
            mainView.add(recipeView);

            GUIView subView = new GUIAutocroppedView(this);
            subView.setSubElementAutoplaceMethod(GUIElement.AP_X_0_TOP_TO_BOTTOM);
            recipeView.add(subView);

            GUIText recipeLabel = new GUIText(this, "Recipe:").setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            subView.addAll(new GUIText(this, "", 0.5), recipeLabel);
            ArrayList<ItemStack> recipes = inventory.getCraftingRecipes();
            recipes.removeIf(ItemStack::isEmpty);
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
            mainView.add(new GUITextSpacer(this, 0, 0.5));

            GUIView traitView = new GUIAutocroppedView(this);
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


//        3. Materials (3 slots)

//        4. Craft button


            //Recalc for immediate render fix, then request options
            recalc();
            Network.WRAPPER.sendToServer(new Network.RequestCraftOptionsPacket(recipe));
        }
    }

    public void updateOptions(ArrayList<String> options)
    {
        traitOptions.clear();
        for (String option : options) traitOptions.add(I18n.translateToLocal(option));

        if (options.size() == 0)
        {
            traitLabel.setText("");
            traitElement.setText("");
            recalc();
            loadingOptions = false;
            return;
        }


        traitLabel.setText("Target Trait: ");
        if (!traitOptions.contains(traitElement.getText()))
        {
            traitElement.setText(traitOptions.get(0));
            recalc();
        }
        loadingOptions = false;
    }

    @Override
    public String title()
    {
        return "Crafting (" + profession + ")";
    }
}
