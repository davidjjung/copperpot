package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.common.items.*;
import com.davigj.copperpot.core.CopperPotConfig;
import com.davigj.copperpot.core.CopperPotMod;
import com.minecraftabnormals.abnormals_core.core.util.registry.ItemSubRegistryHelper;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.registry.ModEffects;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = CopperPotMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CopperPotItems {
    public static final ItemSubRegistryHelper HELPER = CopperPotMod.REGISTRY_HELPER.getItemSubHelper();

    private static boolean isModLoaded(String modid) {
        return (ModList.get().isLoaded(modid));
    }

    public static final RegistryObject<Item> COPPER_POT = HELPER.createItem("copper_pot_block", () -> new BlockItem(
            CopperPotBlocks.COPPER_POT.get(), new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<Item> RAW_MERINGUE = HELPER.createItem("raw_meringue", () -> new Item(
            new Item.Properties().food(Foods.RAW_MERINGUE).tab(ItemGroup.TAB_FOOD)));

    public static final RegistryObject<Item> MERINGUE = HELPER.createItem("meringue", () -> new Item(
            new Item.Properties().food(Foods.MERINGUE).tab(ItemGroup.TAB_FOOD)));

    public static final RegistryObject<Item> AUTUMNAL_AGAR = HELPER.createItem("autumnal_agar", () -> new SeasonalAgar(
            new Item.Properties().food(Foods.AUTUMNAL_AGAR).tab(ItemGroup.TAB_FOOD), new ArrayList<>(CopperPotConfig.COMMON.autumnalExtensionFx.get())));

    public static final RegistryObject<Item> AESTIVAL_AGAR = HELPER.createItem("aestival_agar", () -> new SeasonalAgar(
            new Item.Properties().food(Foods.AESTIVAL_AGAR).tab(ItemGroup.TAB_FOOD), new ArrayList<>(CopperPotConfig.COMMON.aestivalExtensionFx.get())));

    public static final RegistryObject<Item> BRUMAL_AGAR = HELPER.createItem("brumal_agar", () -> new SeasonalAgar(
            new Item.Properties().food(Foods.BRUMAL_AGAR).tab(ItemGroup.TAB_FOOD), new ArrayList<>(CopperPotConfig.COMMON.brumalExtensionFx.get())));

    public static final RegistryObject<Item> VERNAL_AGAR = HELPER.createItem("vernal_agar", () -> new SeasonalAgar(
            new Item.Properties().food(Foods.VERNAL_AGAR).tab(ItemGroup.TAB_FOOD), new ArrayList<>(CopperPotConfig.COMMON.vernalExtensionFx.get())));

    public static final RegistryObject<Item> BAKED_ALASKA_BLOCK = HELPER.createItem("baked_alaska_block", () -> new BlockItem(
            CopperPotBlocks.BAKED_ALASKA_BLOCK.get(), new Item.Properties().stacksTo(1).tab(isModLoaded("neapolitan") ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> BAKED_ALASKA_SLICE = HELPER.createItem("baked_alaska_slice", () -> new BakedAlaskaSlice(
            new Item.Properties().food(Foods.BAKED_ALASKA_SLICE).tab(isModLoaded("neapolitan") ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> PEPPERMINT_BARK_MERINGUE = HELPER.createItem("mint_meringue", () -> new MintMeringue(
            new Item.Properties().food(Foods.PEPPERMINT_BARK_MERINGUE).tab(isModLoaded("neapolitan") ? ItemGroup.TAB_FOOD : null), "effect.neapolitan.berserking", "effect.neapolitan.sugar_rush"));

    public static final RegistryObject<Item> SPICED_APPLE_JAM = HELPER.createItem("spiced_apple_jam", () -> new SpicedAppleJam(
            new Item.Properties().food(Foods.SPICED_APPLE_JAM).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16).tab((isModLoaded("abundance") && isModLoaded("fruitful")) ? ItemGroup.TAB_FOOD : null),
            "effect.fruitful.sustaining", "effect.abundance.supportive"));

    public static final RegistryObject<Item> PORK_SANDWICH = HELPER.createItem("pork_sandwich", () -> new PorkSandwich(
            new Item.Properties().food(Foods.PORK_SANDWICH).tab((isModLoaded("abundance") && isModLoaded("fruitful")) ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> CREEPING_YOGURT = HELPER.createItem("creeping_yogurt", () -> new CreepingYogurt(
            new Item.Properties().food(Foods.CREEPING_YOGURT).stacksTo(16).craftRemainder(Items.BOWL).tab(isModLoaded("savageandravage") ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> ROYAL_JELLY = HELPER.createItem("royal_jelly", () -> new RoyalJelly(
            new Item.Properties().food(Foods.ROYAL_JELLY).tab(isModLoaded("buzzier_bees") ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> MOONCAKE = HELPER.createItem("mooncake", () -> new Mooncake(
            new Item.Properties().food(Foods.MOONCAKE).tab((isModLoaded("neapolitan") && (isModLoaded("bayou_blues") || isModLoaded("environmental"))) ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> SOURDOUGH = HELPER.createItem("sourdough", () -> new Sourdough(
            new Item.Properties().food(Foods.SOURDOUGH).tab(isModLoaded("savageandravage") ? ItemGroup.TAB_FOOD : null)));

    public static final RegistryObject<Item> CARROT_CUPCAKE = HELPER.createItem("carrot_cupcake", () -> new CarrotCupcake(
            new Item.Properties().food(Foods.CARROT_CUPCAKE).tab(ItemGroup.TAB_FOOD), "effect.minecraft.jump_boost"));

    public static final RegistryObject<Item> TROPICAL_MERINGUE = HELPER.createItem("tropical_meringue", () -> new TropicalMeringue(
            new Item.Properties().food(Foods.TROPICAL_MERINGUE).tab((isModLoaded("neapolitan") && isModLoaded("atmospheric")) ? ItemGroup.TAB_FOOD : null), "effect.atmospheric.spitting", "effect.neapolitan.agility"));

    public static final RegistryObject<Item> GUARDIAN_SOUFFLE = HELPER.createItem("guardian_souffle", () -> new GuardianSouffle(
            new Item.Properties().food(Foods.GUARDIAN_SOUFFLE).tab(isModLoaded("upgrade_aquatic") ? ItemGroup.TAB_FOOD : null)));

//    public static final RegistryObject<Item> INCENDIARY_MERINGUE = HELPER.createItem("incendiary_meringue", () -> new IncendiaryMeringue(
//            new Item.Properties().food(Foods.INCENDIARY_MERINGUE).group(ItemGroup.FOOD)));

    static class Foods {
        public static final Food RAW_MERINGUE = (new Food.Builder()).nutrition(1).saturationMod(0.4F).fast().effect(() -> new EffectInstance(Effects.HUNGER, 300), 0.6F).build();
        public static final Food MERINGUE = (new Food.Builder()).nutrition(1).saturationMod(0.6F).fast().build();
        public static final Food BAKED_ALASKA_SLICE = (new Food.Builder()).nutrition(3).saturationMod(0.7F).fast().build();
        public static final Food SPICED_APPLE_JAM = (new Food.Builder()).nutrition(5).saturationMod(0.8F).alwaysEat().build();
        public static final Food PORK_SANDWICH = (new Food.Builder()).nutrition(9).saturationMod(0.8F).build();
        public static final Food PEPPERMINT_BARK_MERINGUE = (new Food.Builder()).nutrition(2).saturationMod(0.5F).fast().build();
        public static final Food CREEPING_YOGURT = (new Food.Builder()).nutrition(5).saturationMod(0.6F).build();
        public static final Food SOURDOUGH = (new Food.Builder()).nutrition(6).saturationMod(0.7F).build();
        public static final Food AUTUMNAL_AGAR = (new Food.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new EffectInstance(
                Effects.ABSORPTION, 100), 0.2F).build();
        public static final Food AESTIVAL_AGAR = (new Food.Builder()).nutrition(5).saturationMod(0.5F).effect(() -> new EffectInstance(
                Effects.NIGHT_VISION, 100), 0.2F).build();
        public static final Food BRUMAL_AGAR = (new Food.Builder()).nutrition(3).saturationMod(0.7F).effect(() -> new EffectInstance(
                ModEffects.COMFORT.get(), 100), 0.2F).build();
        public static final Food VERNAL_AGAR = (new Food.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new EffectInstance(
                Effects.REGENERATION, 100), 0.2F).build();
        public static final Food ROYAL_JELLY = (new Food.Builder()).nutrition(6).saturationMod(0.6F).effect(() -> new EffectInstance(
                Effects.HUNGER, 300), 0.5F).build();
        public static final Food MOONCAKE = (new Food.Builder()).nutrition(4).saturationMod(0.7F).effect(() -> new EffectInstance(
                Effects.POISON, 200, 1), 0.6F).build();
        public static final Food CARROT_CUPCAKE = (new Food.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new EffectInstance(
                Effects.JUMP, 200), 0.2F).build();
        public static final Food TROPICAL_MERINGUE = (new Food.Builder()).nutrition(3).saturationMod(0.3F).fast().build();
        public static final Food GUARDIAN_SOUFFLE = (new Food.Builder()).nutrition(4).saturationMod(0.3F).build();
        public static final Food INCENDIARY_MERINGUE = (new Food.Builder()).nutrition(2).saturationMod(0.5F).fast().build();
    }
}