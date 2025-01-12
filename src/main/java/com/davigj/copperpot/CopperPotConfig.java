package com.davigj.copperpot;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.List;

public class CopperPotConfig {

   public static final ForgeConfigSpec COMMON_SPEC;
   public static final CopperPotConfig.Common COMMON;

   public static final ForgeConfigSpec CLIENT_SPEC;
   public static final Client CLIENT;

   static {
      Pair<Common, ForgeConfigSpec> commonSpecPair = (new ForgeConfigSpec.Builder()).configure(CopperPotConfig.Common::new);
      COMMON_SPEC = (ForgeConfigSpec) commonSpecPair.getRight();
      COMMON = (CopperPotConfig.Common) commonSpecPair.getLeft();

      Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
      CLIENT_SPEC = clientSpecPair.getRight();
      CLIENT = clientSpecPair.getLeft();
   }

   public static class Common {
      public final ForgeConfigSpec.DoubleValue copperFumeRadius;
      public final ForgeConfigSpec.BooleanValue recipeReg;
      public final ForgeConfigSpec.BooleanValue heatless;

      Common(ForgeConfigSpec.Builder builder) {
         copperFumeRadius = builder.comment("Horizontal radius for copper pots granting without fume inhibitors involved. 0 block distance min, 5 max")
                 .defineInRange("copperFumeRadius", 2.0D, 0.0D, 5.0D);
         recipeReg = builder.comment("Do copper pots automatically register cooking pot recipes with less than 4 slots")
                 .define("recipeRegistration", false);
         heatless = builder.comment("Do copper pots function without any heat sources")
                 .define("heatless", false);
      }
   }


   public static class Client {
      public final ForgeConfigSpec.ConfigValue<Boolean> potParticles;

      public Client(ForgeConfigSpec.Builder builder) {
         builder.push("client");
         potParticles = builder.comment("Copper pots emit effect particles when cooking or ready to cook").define("Cooking particles", true);
         builder.pop();
      }
   }
}
