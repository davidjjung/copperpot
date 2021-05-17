package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.core.CopperPotMod;
import com.minecraftabnormals.abnormals_core.core.util.registry.ItemSubRegistryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CopperPotMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CopperPotItems {
	public static final ItemSubRegistryHelper HELPER = CopperPotMod.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> COPPER_POT = HELPER.createItem("copper_pot_block", () -> new BlockItem(
			CopperPotBlocks.COPPER_POT.get(), new Item.Properties().maxStackSize(16).group(ItemGroup.BUILDING_BLOCKS)));
}