package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.item.*;
import com.davigj.copperpot.core.tags.CPMobEffectTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class CPItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CopperPot.MODID);

   public static final RegistryObject<Item> COPPER_POT = ITEMS.register("copper_pot", () -> new CopperPotItem(
           CPBlocks.COPPER_POT.get(), new Item.Properties().stacksTo(1)));

   public static final RegistryObject<Item> MERINGUE_BLOCK = ITEMS.register("meringue_block", () -> new BlockItem(
           CPBlocks.MERINGUE_BLOCK.get(), new Item.Properties()));

   public static final RegistryObject<Item> RAW_MERINGUE = ITEMS.register("raw_meringue", () -> new Item(
           new Item.Properties().food(Foods.RAW_MERINGUE)));

   public static final RegistryObject<Item> MERINGUE = ITEMS.register("meringue", () -> new Item(
           new Item.Properties().food(Foods.MERINGUE)));

   public static final RegistryObject<Item> AUTUMNAL_AGAR = ITEMS.register("autumnal_agar", () -> new SeasonalAgarItem(
           new Item.Properties().food(Foods.AUTUMNAL_AGAR), CPMobEffectTags.AUTUMNAL));

   public static final RegistryObject<Item> AESTIVAL_AGAR = ITEMS.register("aestival_agar", () -> new SeasonalAgarItem(
           new Item.Properties().food(Foods.AESTIVAL_AGAR), CPMobEffectTags.AESTIVAL));

   public static final RegistryObject<Item> BRUMAL_AGAR = ITEMS.register("brumal_agar", () -> new SeasonalAgarItem(
           new Item.Properties().food(Foods.BRUMAL_AGAR), CPMobEffectTags.BRUMAL));

   public static final RegistryObject<Item> VERNAL_AGAR = ITEMS.register("vernal_agar", () -> new SeasonalAgarItem(
           new Item.Properties().food(Foods.VERNAL_AGAR), CPMobEffectTags.VERNAL));

   public static final RegistryObject<Item> BAKED_ALASKA_BLOCK = ITEMS.register("baked_alaska_block", () -> new BlockItem(
           CPBlocks.BAKED_ALASKA_BLOCK.get(), new Item.Properties().stacksTo(1)));

   public static final RegistryObject<Item> BAKED_ALASKA_SLICE = ITEMS.register("baked_alaska_slice", () -> new BakedAlaskaSlice(
           new Item.Properties().food(Foods.BAKED_ALASKA_SLICE)));

   public static final RegistryObject<Item> PEPPERMINT_BARK_MERINGUE = ITEMS.register("mint_meringue", () -> new MintMeringue(
           new Item.Properties().food(Foods.PEPPERMINT_BARK_MERINGUE), "effect.neapolitan.berserking", "effect.neapolitan.sugar_rush"));

   public static final RegistryObject<Item> SPICED_APPLE_JAM = ITEMS.register("spiced_apple_jam", () -> new SpicedAppleJam(
           new Item.Properties().food(Foods.SPICED_APPLE_JAM).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16),
           "effect.fruitful.sustaining", "effect.abundance.supportive"));

   public static final RegistryObject<Item> PORK_SANDWICH = ITEMS.register("pork_sandwich", () -> new PorkSandwich(
           new Item.Properties().food(Foods.PORK_SANDWICH)));

   public static final RegistryObject<Item> CREEPING_YOGURT = ITEMS.register("creeping_yogurt", () -> new CreepingYogurt(
           new Item.Properties().food(Foods.CREEPING_YOGURT).stacksTo(16).craftRemainder(Items.BOWL)));

   public static final RegistryObject<Item> ROYAL_JELLY = ITEMS.register("royal_jelly", () -> new RoyalJelly(
           new Item.Properties().food(Foods.ROYAL_JELLY)));

   public static final RegistryObject<Item> MOONCAKE = ITEMS.register("mooncake", () -> new Mooncake(
           new Item.Properties().food(Foods.MOONCAKE)));

   public static final RegistryObject<Item> SOURDOUGH = ITEMS.register("sourdough", () -> new Sourdough(
           new Item.Properties().food(Foods.SOURDOUGH)));

   public static final RegistryObject<Item> CARROT_CUPCAKE = ITEMS.register("carrot_cupcake", () -> new CarrotCupcake(
           new Item.Properties().food(Foods.CARROT_CUPCAKE), "effect.minecraft.jump_boost"));

   public static final RegistryObject<Item> TROPICAL_MERINGUE = ITEMS.register("tropical_meringue", () -> new TropicalMeringue(
           new Item.Properties().food(Foods.TROPICAL_MERINGUE), "effect.atmospheric.spitting", "effect.neapolitan.agility"));

   public static final RegistryObject<Item> GUARDIAN_SOUFFLE = ITEMS.register("guardian_souffle", () -> new GuardianSouffle(
           new Item.Properties().food(Foods.GUARDIAN_SOUFFLE)));

    public static final RegistryObject<Item> INCENDIARY_MERINGUE = ITEMS.register("incendiary_meringue", () -> new IncendiaryMeringue(
            new Item.Properties().food(Foods.INCENDIARY_MERINGUE)));


   static class Foods {
      public static final FoodProperties RAW_MERINGUE = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.4F).fast().effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300), 0.6F).build();
      public static final FoodProperties MERINGUE = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.6F).fast().build();
      public static final FoodProperties BAKED_ALASKA_SLICE = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.7F).fast().build();
      public static final FoodProperties SPICED_APPLE_JAM = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.8F).alwaysEat().build();
      public static final FoodProperties PORK_SANDWICH = (new FoodProperties.Builder()).nutrition(9).saturationMod(0.8F).build();
      public static final FoodProperties PEPPERMINT_BARK_MERINGUE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.5F).fast().build();
      public static final FoodProperties CREEPING_YOGURT = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).build();
      public static final FoodProperties SOURDOUGH = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.7F).build();
      public static final FoodProperties AUTUMNAL_AGAR = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new MobEffectInstance(
              MobEffects.ABSORPTION, 100), 0.2F).build();
      public static final FoodProperties AESTIVAL_AGAR = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.5F).effect(() -> new MobEffectInstance(
              MobEffects.NIGHT_VISION, 100), 0.2F).build();
      public static final FoodProperties BRUMAL_AGAR = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.7F).effect(() -> new MobEffectInstance(
              ModEffects.COMFORT.get(), 100), 0.2F).build();
      public static final FoodProperties VERNAL_AGAR = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new MobEffectInstance(
              MobEffects.REGENERATION, 100), 0.2F).build();
      public static final FoodProperties ROYAL_JELLY = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.6F).effect(() -> new MobEffectInstance(
              MobEffects.HUNGER, 300), 0.5F).build();
      public static final FoodProperties MOONCAKE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.7F).effect(() -> new MobEffectInstance(
              MobEffects.POISON, 200, 1), 0.6F).build();
      public static final FoodProperties CARROT_CUPCAKE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new MobEffectInstance(
              MobEffects.JUMP, 200), 0.2F).build();
      public static final FoodProperties TROPICAL_MERINGUE = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).fast().build();
      public static final FoodProperties GUARDIAN_SOUFFLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
      public static final FoodProperties INCENDIARY_MERINGUE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.5F).fast().build();
   }

}
