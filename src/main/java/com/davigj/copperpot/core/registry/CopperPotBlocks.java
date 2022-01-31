package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.common.blocks.CopperPotBlock;
import com.davigj.copperpot.common.blocks.BakedAlaskaBlock;
import com.davigj.copperpot.common.blocks.MeringueBlock;
import com.davigj.copperpot.core.CopperPotMod;
import com.minecraftabnormals.abnormals_core.core.util.registry.BlockSubRegistryHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CopperPotMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CopperPotBlocks {
	public static final BlockSubRegistryHelper HELPER = CopperPotMod.REGISTRY_HELPER.getBlockSubHelper();

	public static final RegistryObject<Block> COPPER_POT = HELPER.createBlockNoItem("copper_pot_block", () -> new CopperPotBlock(
			AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 6.0F).sound(
					SoundType.LANTERN)));

	public static final RegistryObject<Block> BAKED_ALASKA_BLOCK = HELPER.createBlockNoItem("baked_alaska_block", () -> new BakedAlaskaBlock(
			AbstractBlock.Properties.of(Material.CAKE, MaterialColor.TERRACOTTA_WHITE).strength(
					0.5F).sound(SoundType.WOOL)));

	public static final RegistryObject<Block> MERINGUE_BLOCK = HELPER.createBlock("meringue_block", () -> new MeringueBlock(
			AbstractBlock.Properties.of(Material.GRASS, MaterialColor.TERRACOTTA_WHITE).strength(
					0.1F).sound(SoundType.SLIME_BLOCK)), ItemGroup.TAB_BUILDING_BLOCKS);
}
