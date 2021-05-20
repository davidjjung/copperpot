package com.davigj.copperpot.common.crafting;

import com.davigj.copperpot.core.CopperPotMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopperPotRecipe implements IRecipe<IInventory> {
    public static IRecipeType<CopperPotRecipe> TYPE = IRecipeType.register("copperpot:cooking");
    public static final CopperPotRecipe.Serializer SERIALIZER = new CopperPotRecipe.Serializer();
    public static final int INPUT_SLOTS = 3;
    private final ResourceLocation id;
    private final String group;
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int cookTime;
    private final boolean effecttrue;
    private final String effect;
    private final int effectDuration;
    private final int effectAmplifier;

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    public CopperPotRecipe(ResourceLocation id, String group, NonNullList<Ingredient> inputItems, ItemStack output, ItemStack container, float experience, int cookTime, boolean effecttrue, String effect, int effectDuration, int effectAmplifier) {
        this.id = id;
        this.group = group;
        this.inputItems = inputItems;
        this.output = output;
        if (!container.isEmpty()) {
            this.container = container;
        } else if (!output.getContainerItem().isEmpty()) {
            this.container = output.getContainerItem();
        } else {
            this.container = ItemStack.EMPTY;
        }
        this.experience = experience;
        this.cookTime = cookTime;
        this.effecttrue = effecttrue;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
//        LOGGER.debug(effecttrue + ", " + effect + ", " + effectDuration + ", " + effectAmplifier);
    }

    public ResourceLocation getId() {
        return this.id;
    }
    public String getGroup() {
        return this.group;
    }
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }
    public ItemStack getRecipeOutput() {
        return this.output;
    }
    public ItemStack getOutputContainer() {
        return this.container;
    }
    public ItemStack getCraftingResult(IInventory inv) {
        return this.output.copy();
    }
    public float getExperience() {
        return this.experience;
    }
    public int getCookTime() {
        return this.cookTime;
    }
    public boolean getEffectTrue() { return this.effecttrue;}
    public String getEffect() {return this.effect;}
    public int getEffectDuration() { return this.effectDuration;}
    public int getEffectAmplifier() { return this.effectAmplifier;}
    public boolean matches(IInventory inv, World worldIn) {
        List<ItemStack> inputs = new ArrayList();
        int i = 0;
        // originally j < 6
        for(int j = 0; j < 3; ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        return i == this.inputItems.size() && RecipeMatcher.findMatches(inputs, this.inputItems) != null;
    }

    public boolean canFit(int width, int height) {
        return width * height >= this.inputItems.size();
    }

    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public IRecipeType<?> getType() {
        return TYPE;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CopperPotRecipe> {
        Serializer() {
            this.setRegistryName(new ResourceLocation("copperpot", "cooking"));
        }

        public CopperPotRecipe read(ResourceLocation recipeId, JsonObject json) {
            String groupIn = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> inputItemsIn = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputItemsIn.size() > 3) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is 3");
            } else {
                ItemStack outputIn = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);
                ItemStack container = JSONUtils.hasField(json, "container") ? CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "container"), true) : ItemStack.EMPTY;
                float experienceIn = JSONUtils.getFloat(json, "experience", 0.0F);
                int cookTimeIn = JSONUtils.getInt(json, "cookingtime", 100);
                boolean effecttrue = JSONUtils.getBoolean(json, "effecttrue", false);
                String effect = JSONUtils.getString(json, "effect", "");
                int effectduration = JSONUtils.getInt(json, "effectduration", 100);
                int effectamplifier = JSONUtils.getInt(json, "effectamplifier", 0);

                return new CopperPotRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, effecttrue, effect, effectduration, effectamplifier);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
                if (!ingredient.hasNoMatchingItems()) {
                    nonnulllist.add(ingredient);
                }
            }
            return nonnulllist;
        }

        @Nullable
        public CopperPotRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String groupIn = buffer.readString(32767);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < inputItemsIn.size(); ++j) {
                inputItemsIn.set(j, Ingredient.read(buffer));
            }

            ItemStack outputIn = buffer.readItemStack();
            ItemStack container = buffer.readItemStack();
            float experienceIn = buffer.readFloat();
            int cookTimeIn = buffer.readVarInt();
            boolean effecttrue = buffer.readBoolean();
            String effect = buffer.readString();
            int effectduration = buffer.readVarInt();
            int effectamplifier = buffer.readVarInt();
            return new CopperPotRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, effecttrue, effect, effectduration, effectamplifier);
        }

        public void write(PacketBuffer buffer, CopperPotRecipe recipe) {
            buffer.writeString(recipe.group);
            buffer.writeVarInt(recipe.inputItems.size());
            Iterator var3 = recipe.inputItems.iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.output);
            buffer.writeItemStack(recipe.container);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
            buffer.writeBoolean(recipe.effecttrue);
            buffer.writeString(recipe.effect);
            buffer.writeVarInt(recipe.effectDuration);
            buffer.writeVarInt(recipe.effectAmplifier);
        }
    }
}