package com.davigj.copperpot.common.item;

import com.davigj.copperpot.CopperPotConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class Mooncake extends Item {
   public Mooncake(Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
//      for (String i : CopperPotConfig.COMMON.mooncakeBadReactDims.get()) {
//         if (pLivingEntity.getCommandSenderWorld().dimensionType().effectsLocation().toString().equals(i)) {
//            pLivingEntity.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
//            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 480));
//         } else {
//            moonlight(pLivingEntity, pLevel);
//         }
//      }
      return pStack;
   }

   public void moonlight(LivingEntity entity, Level levelIn) {
      long daytime = levelIn.getDayTime() % 24000;
      long moonProxTime = Math.abs(daytime - 6000);
      double moon = Math.abs(4 - levelIn.getMoonPhase());
      if (moonProxTime > 12000) {
         moonProxTime = Math.abs(24000 - moonProxTime);
      }
      if (levelIn.isDay()) {
         moon = 0.7;
      } else {
         moon = 1 + (0.1 * moon);
      }
//        LOGGER.debug("daytime: " + daytime + " moon multiplier: " + moon + " moon prox time: " + moonProxTime);
      if ( ModList.get().isLoaded("neapolitan")) {
         entity.addEffect(new MobEffectInstance(
                 getCompatEffect("neapolitan", new ResourceLocation(
                         "neapolitan", "harmony")).get(), (int) ((0.11 * moonProxTime) * moon)));
      }
   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }
   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      MutableComponent tip = Component.translatable("copperpot.tooltip.mooncake.tip");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.BLUE));
   }
}
