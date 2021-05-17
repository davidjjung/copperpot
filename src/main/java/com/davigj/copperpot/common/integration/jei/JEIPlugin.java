package com.davigj.copperpot.common.integration.jei;

import com.davigj.copperpot.client.gui.CopperPotScreen;
import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.common.integration.jei.cooking.CookingRecipeCategory;
import com.davigj.copperpot.common.tile.container.CopperPotContainer;
import com.davigj.copperpot.core.CopperPotMod;
import com.davigj.copperpot.core.registry.CopperPotBlocks;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin
{
    private static final ResourceLocation ID = new ResourceLocation(CopperPotMod.MOD_ID, "jei_plugin");
    private static final Minecraft MC = Minecraft.getInstance();

    private static List<IRecipe<?>> findRecipesByType(IRecipeType<?> type) {
        return MC.world
                .getRecipeManager()
                .getRecipes()
                .stream()
                .filter(r -> r.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new CookingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(findRecipesByType(CopperPotRecipe.TYPE), CookingRecipeCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(CopperPotBlocks.COPPER_POT.get().asItem()), CookingRecipeCategory.UID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CopperPotScreen.class, 89, 25, 24, 17, CookingRecipeCategory.UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CopperPotContainer.class, CookingRecipeCategory.UID, 0, 3, 6, 36);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
