package com.davigj.copperpot.common.loot.functions;

import com.davigj.copperpot.common.tile.CopperPotTileEntity;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopyMealFunction extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation("copperpot", "copy_meal");

    private CopyMealFunction(ILootCondition[] conditions) {
        super(conditions);
    }

    public static Builder<?> builder() {
        return builder(CopyMealFunction::new);
    }

    protected ItemStack doApply(ItemStack stack, LootContext context) {
        TileEntity tile = (TileEntity)context.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof CopperPotTileEntity) {
            CompoundNBT tag = ((CopperPotTileEntity)tile).writeMeal(new CompoundNBT());
            if (!tag.isEmpty()) {
                stack.setTagInfo("BlockEntityTag", tag);
            }
        }

        return stack;
    }

    @Nullable
    public LootFunctionType getFunctionType() {
        return null;
    }

    public static class Serializer extends net.minecraft.loot.LootFunction.Serializer<CopyMealFunction> {
        public Serializer() {
        }

        public CopyMealFunction deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditions) {
            return new CopyMealFunction(conditions);
        }
    }
}
