package com.davigj.copperpot;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CopperPotConfig {

   public static final ForgeConfigSpec COMMON_SPEC;
   public static final CopperPotConfig.Common COMMON;

   static {
      Pair<Common, ForgeConfigSpec> commonSpecPair = (new ForgeConfigSpec.Builder()).configure(CopperPotConfig.Common::new);
      COMMON_SPEC = (ForgeConfigSpec) commonSpecPair.getRight();
      COMMON = (CopperPotConfig.Common) commonSpecPair.getLeft();
   }

   public static class Common {
//      public final ForgeConfigSpec.ConfigValue<List<String>> mooncakeBadReactDims;
      public final ForgeConfigSpec.DoubleValue copperFumeRadius;
      public final ForgeConfigSpec.BooleanValue recipeReg;

      Common(ForgeConfigSpec.Builder builder) {
//         mooncakeBadReactDims = builder.comment("A list of dimensions in which mooncakes apply adverse effects " +
//                 "to the player when consumed, usually due to the lack of a moon.").define("mooncakeBadReactDims",
//                 Lists.newArrayList("minecraft:the_nether", "minecraft:the_end"));
         copperFumeRadius = builder.comment("Horizontal radius for copper pots granting without fume inhibitors involved. 0 block distance min, 5 max")
                 .defineInRange("copperFumeRadius", 2.0D, 0.0D, 5.0D);
         recipeReg = builder.comment("Do copper pots automatically register cooking pot recipes with less than 4 slots")
                 .define("recipeRegistration", false);
      }
   }
}
