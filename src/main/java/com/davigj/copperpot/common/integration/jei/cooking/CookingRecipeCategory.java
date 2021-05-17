package com.davigj.copperpot.common.integration.jei.cooking;

import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.core.registry.CopperPotBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CookingRecipeCategory implements IRecipeCategory<CopperPotRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("copperpot", "cooking");
    protected final IDrawable heatIndicator;
    protected final IDrawableAnimated arrow;
    private final String title = I18n.format("copperpot.jei.cooking", new Object[0]);
    private final IDrawable background;
    private final IDrawable icon;

    public CookingRecipeCategory(IGuiHelper helper) {
        ResourceLocation backgroundImage = new ResourceLocation("copperpot", "textures/gui/copper_pot.png");
        this.background = helper.createDrawable(backgroundImage, 29, 16, 117, 57);
        this.icon = helper.createDrawableIngredient(new ItemStack((IItemProvider) CopperPotBlocks.COPPER_POT.get().asItem()));
        this.heatIndicator = helper.createDrawable(backgroundImage, 176, 0, 17, 15);
        this.arrow = helper.drawableBuilder(backgroundImage, 176, 15, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    }

    public ResourceLocation getUid() {
        return UID;
    }

    public Class<? extends CopperPotRecipe> getRecipeClass() {
        return CopperPotRecipe.class;
    }

    public String getTitle() {
        return this.title;
    }

    public IDrawable getBackground() {
        return this.background;
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public void setIngredients(CopperPotRecipe copperPotRecipe, IIngredients ingredients) {
        List<Ingredient> inputAndContainer = new ArrayList(copperPotRecipe.getIngredients());
        inputAndContainer.add(Ingredient.fromStacks(new ItemStack[]{copperPotRecipe.getOutputContainer()}));
        ingredients.setInputIngredients(inputAndContainer);
        ingredients.setOutput(VanillaTypes.ITEM, copperPotRecipe.getRecipeOutput());
    }

    public void setRecipe(IRecipeLayout recipeLayout, CopperPotRecipe recipe, IIngredients ingredients) {
        int MEAL_DISPLAY = 3;
        int CONTAINER_INPUT = 4;
        int OUTPUT = 5;
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        int borderSlotSize = 18;

        for(int row = 0; row < 1; ++row) {
            for(int column = 0; column < 3; ++column) {
                int inputIndex = (row * 3) + column;
                if (inputIndex < recipeIngredients.size()) {
//                    itemStacks.init(inputIndex, true, column * borderSlotSize, row * borderSlotSize);
                    itemStacks.init(inputIndex, true, column * borderSlotSize, (row * borderSlotSize)+9);
                    itemStacks.set(inputIndex, Arrays.asList(((Ingredient)recipeIngredients.get(inputIndex)).getMatchingStacks()));
                }
            }
        }

        itemStacks.init(MEAL_DISPLAY, false, 94, 9);
        itemStacks.set(MEAL_DISPLAY, recipe.getRecipeOutput().getStack());

        if (!recipe.getOutputContainer().isEmpty()) {
            itemStacks.init(CONTAINER_INPUT, false, 62, 38);
            itemStacks.set(CONTAINER_INPUT, recipe.getOutputContainer());
        }
        itemStacks.init(OUTPUT, false, 94, 38);
        itemStacks.set(OUTPUT, recipe.getRecipeOutput().getStack());
    }

    public void draw(CopperPotRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        this.arrow.draw(matrixStack, 60, 9);
//        this.heatIndicator.draw(matrixStack, 18, 39);
        this.heatIndicator.draw(matrixStack, 19, 32);

    }
}
