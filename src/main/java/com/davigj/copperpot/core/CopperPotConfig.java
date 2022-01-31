package com.davigj.copperpot.core;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CopperPotConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CopperPotConfig.Common COMMON;

    static {
        Pair<CopperPotConfig.Common, ForgeConfigSpec> commonSpecPair = (new ForgeConfigSpec.Builder()).configure(CopperPotConfig.Common::new);
        COMMON_SPEC = (ForgeConfigSpec) commonSpecPair.getRight();
        COMMON = (CopperPotConfig.Common) commonSpecPair.getLeft();
    }

    public static class Common {
        // initialize
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> mooncakeBadReactDims;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> autumnalExtensionFx;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> aestivalExtensionFx;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> brumalExtensionFx;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> vernalExtensionFx;
        public final ForgeConfigSpec.DoubleValue copperFumeRadius;

        // define
        Common(ForgeConfigSpec.Builder builder) {
            mooncakeBadReactDims = builder.comment("A list of dimensions in which mooncakes apply adverse effects " +
                    "to the player when consumed, usually due to the lack of a moon.").define("mooncakeBadReactDims",
                    Lists.newArrayList("minecraft:the_nether", "minecraft:the_end"));
            autumnalExtensionFx = builder.comment("A list of effects which will be extended upon consumption of autumnal agar.")
                    .define("autumnalExtensionFx", Lists.newArrayList("effect.minecraft.resistance",
                            "effect.farmersdelight.nourished", "effect.minecraft.absorption", "effect.atmospheric.persistence"));
            aestivalExtensionFx = builder.comment("A list of effects which will be extended upon consumption of aestival agar.")
                    .define("aestivalExtensionFx", Lists.newArrayList("effect.minecraft.night_vision",
                            "effect.atmospheric.relief", "effect.minecraft.water_breathing", "effect.minecraft.strength"));
            brumalExtensionFx = builder.comment("A list of effects which will be extended upon consumption of brumal agar.")
                    .define("brumalExtensionFx", Lists.newArrayList("effect.farmersdelight.comfort",
                            "effect.minecraft.invisibility", "effect.minecraft.fire_resistance", "effect.minecraft.slow_falling"));
            vernalExtensionFx = builder.comment("A list of effects which will be extended upon consumption of vernal agar.")
                    .define("vernalExtensionFx", Lists.newArrayList("effect.minecraft.regeneration",
                            "effect.minecraft.jump_boost", "effect.minecraft.haste", "effect.upgrade_aquatic.vibing"));
            copperFumeRadius = builder.comment("The horizontal radius for which copper pots will grant effects with no fume inhibitors involved.")
                    .defineInRange("copperFumeRadius", 3.0D, 1.0D, Double.MAX_VALUE);
        }
    }
}