package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.common.items.*;
import com.davigj.copperpot.core.CopperPotMod;
import com.minecraftabnormals.abnormals_core.core.util.registry.ItemSubRegistryHelper;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.registry.ModEffects;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = CopperPotMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CopperPotItems {
	public static final ItemSubRegistryHelper HELPER = CopperPotMod.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> COPPER_POT = HELPER.createItem("copper_pot_block", () -> new BlockItem(
			CopperPotBlocks.COPPER_POT.get(), new Item.Properties().maxStackSize(16).group(ItemGroup.BUILDING_BLOCKS)));

	public static final RegistryObject<Item> RAW_MERINGUE = HELPER.createItem("raw_meringue", () -> new Item(
			new Item.Properties().food(Foods.RAW_MERINGUE).group(ItemGroup.FOOD)));

	public static final RegistryObject<Item> MERINGUE = HELPER.createItem("meringue", () -> new Item(
			new Item.Properties().food(Foods.MERINGUE).group(ItemGroup.FOOD)));

	public static final RegistryObject<Item> AUTUMNAL_AGAR = HELPER.createItem("autumnal_agar", () -> new SeasonalAgar(
			new Item.Properties().food(Foods.AUTUMNAL_AGAR).group(ItemGroup.FOOD), "effect.minecraft.resistance",
			"effect.farmersdelight.nourished", "effect.minecraft.absorption", "effect.atmospheric.persistence"));

	public static final RegistryObject<Item> AESTIVAL_AGAR = HELPER.createItem("aestival_agar", () -> new SeasonalAgar(
			new Item.Properties().food(Foods.AESTIVAL_AGAR).group(ItemGroup.FOOD), "effect.minecraft.night_vision",
			"effect.atmospheric.gelled", "effect.minecraft.water_breathing", "effect.minecraft.strength"));

	public static final RegistryObject<Item> BRUMAL_AGAR = HELPER.createItem("brumal_agar", () -> new SeasonalAgar(
			new Item.Properties().food(Foods.BRUMAL_AGAR).group(ItemGroup.FOOD), "effect.farmersdelight.comfort",
			"effect.minecraft.invisibility", "effect.minecraft.fire_resistance", "effect.minecraft.slow_falling"));

	public static final RegistryObject<Item> VERNAL_AGAR = HELPER.createItem("vernal_agar", () -> new Item(
			new Item.Properties().food(Foods.VERNAL_AGAR).group(ItemGroup.FOOD)));

	public static final RegistryObject<Item> BAKED_ALASKA_BLOCK = HELPER.createItem("baked_alaska_block", () -> new BlockItem(
			CopperPotBlocks.BAKED_ALASKA_BLOCK.get(), new Item.Properties().maxStackSize(1).group(ItemGroup.FOOD)));

	public static final RegistryObject<Item> BAKED_ALASKA_SLICE = HELPER.createItem("baked_alaska_slice", () -> new BakedAlaskaSlice(
			new Item.Properties().food(Foods.BAKED_ALASKA_SLICE).group(ItemGroup.FOOD)));

	public static final RegistryObject<Item> ADZUKI_PASTE = HELPER.createItem("adzuki_paste", () -> new SingleAdditiveBottled(
			new Item.Properties().food(Foods.ADZUKI_PASTE).containerItem(Items.GLASS_BOTTLE).maxStackSize(16).group(ItemGroup.FOOD), "effect.neapolitan.harmony"));

	public static final RegistryObject<Item> MAPLE_BACON_FUDGE = HELPER.createItem("maple_bacon_fudge", () -> new SingleAdditive(
			new Item.Properties().food(Foods.MAPLE_BACON_FUDGE).group(ItemGroup.FOOD), "effect.minecraft.resistance"));

	public static final RegistryObject<Item> PEPPERMINT_BARK_MERINGUE = HELPER.createItem("mint_meringue", () -> new MintMeringue(
			new Item.Properties().food(Foods.PEPPERMINT_BARK_MERINGUE).group(ItemGroup.FOOD), "effect.neapolitan.berserking", "effect.neapolitan.sugar_rush"));

	public static final RegistryObject<Item> SPICED_APPLE_JAM = HELPER.createItem("spiced_apple_jam", () -> new DoubleAdditiveBottled(
			new Item.Properties().food(Foods.SPICED_APPLE_JAM).containerItem(Items.GLASS_BOTTLE).maxStackSize(16).group(ItemGroup.FOOD),
			"effect.fruitful.sustaining", "effect.abundance.supportive"));

	public static final RegistryObject<Item> PORK_SANDWICH = HELPER.createItem("pork_sandwich", () -> new Item(
			new Item.Properties().food(Foods.PORK_SANDWICH).group(ItemGroup.FOOD)));

	private static boolean modExists(String modId) {return ModList.get().isLoaded(modId);}

	static class Foods {
		private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
			return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
		}
		public static final Food RAW_MERINGUE = (new Food.Builder()).hunger(1).saturation(1.0F).fastToEat().effect(() -> new EffectInstance(Effects.HUNGER, 300), 0.6F).build();
		public static final Food MERINGUE = (new Food.Builder()).hunger(2).saturation(0.8F).fastToEat().build();
		public static final Food BAKED_ALASKA_SLICE = (new Food.Builder()).hunger(3).saturation(0.8F).fastToEat().build();
		public static final Food ADZUKI_PASTE = (new Food.Builder()).hunger(4).saturation(0.5F).setAlwaysEdible().effect(() -> new EffectInstance(
				getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "harmony")).get(), 80), 0.8F).build();
		public static final Food MAPLE_BACON_FUDGE = (new Food.Builder()).hunger(5).saturation(0.5F).effect(() -> new EffectInstance(Effects.RESISTANCE, 100, 1), 0.8F).build();
		public static final Food SPICED_APPLE_JAM = (new Food.Builder()).hunger(5).saturation(0.8F).effect(() -> new EffectInstance(
				getCompatEffect("fruitful", new ResourceLocation("fruitful", "sustaining")).get(), 300, 0), 0.8F)
				.effect(() -> new EffectInstance(getCompatEffect("abundance", new ResourceLocation("abundance", "supportive"))
						.get(), 160, 0), 0.8F).setAlwaysEdible().build();
		public static final Food PORK_SANDWICH = (new Food.Builder()).hunger(10).saturation(0.8F).build();
		public static final Food PEPPERMINT_BARK_MERINGUE = (new Food.Builder()).hunger(3).saturation(0.5F).fastToEat().build();
		// .effect(() -> new EffectInstance(
		//				getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "berserking")).get(), 100, 0), 0.8F)
		//				.effect(() -> new EffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "sugar_rush"))
		//						.get(), 140, 1), 0.8F)
		public static final Food AUTUMNAL_AGAR = (new Food.Builder()).hunger(4).saturation(0.6F).effect(() -> new EffectInstance(
				Effects.ABSORPTION, 100), 0.2F).build();
		public static final Food AESTIVAL_AGAR = (new Food.Builder()).hunger(5).saturation(0.5F).effect(() -> new EffectInstance(
				Effects.NIGHT_VISION, 100), 0.2F).build();
		public static final Food BRUMAL_AGAR = (new Food.Builder()).hunger(3).saturation(0.7F).effect(() -> new EffectInstance(
				ModEffects.COMFORT.get(), 100), 0.2F).build();
		public static final Food VERNAL_AGAR = (new Food.Builder()).hunger(4).saturation(0.6F).effect(() -> new EffectInstance(
				Effects.JUMP_BOOST, 100), 0.2F).build();
	}
}