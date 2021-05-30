package com.davigj.copperpot.core.setup;

import com.davigj.copperpot.common.loot.functions.CopyMealFunction;
import com.davigj.copperpot.core.CopperPotMod;
import com.davigj.copperpot.core.registry.CopperPotItems;
import net.minecraft.block.ComposterBlock;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber(modid = CopperPotMod.MOD_ID)
@ParametersAreNonnullByDefault
public class CommonEventHandler {

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
        {
            registerCompostables();
        });
        LootFunctionManager.func_237451_a_(CopyMealFunction.ID.toString(), new CopyMealFunction.Serializer());
    }

    public static void registerCompostables() {
        ComposterBlock.CHANCES.put(CopperPotItems.AUTUMNAL_AGAR.get(), 0.65F);
        ComposterBlock.CHANCES.put(CopperPotItems.AESTIVAL_AGAR.get(), 0.65F);
        ComposterBlock.CHANCES.put(CopperPotItems.VERNAL_AGAR.get(), 0.65F);
        ComposterBlock.CHANCES.put(CopperPotItems.BRUMAL_AGAR.get(), 0.65F);
    }

}
