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
			AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2.0F, 6.0F).sound(
					SoundType.LANTERN)));

	public static final RegistryObject<Block> BAKED_ALASKA_BLOCK = HELPER.createBlockNoItem("baked_alaska_block", () -> new BakedAlaskaBlock(
			AbstractBlock.Properties.create(Material.CAKE, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(
					0.5F).sound(SoundType.CLOTH)));

	public static final RegistryObject<Block> MERINGUE_BLOCK = HELPER.createBlock("meringue_block", () -> new MeringueBlock(
			AbstractBlock.Properties.create(Material.ORGANIC, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(
					0.1F).sound(SoundType.SLIME)), ItemGroup.BUILDING_BLOCKS);
}
