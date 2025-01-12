package com.davigj.copperpot.common.item;

import com.davigj.copperpot.CopperPotConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class MintMeringue extends Item {
   String effect1;
   String effect2;

   public MintMeringue(Item.Properties properties, String effect1, String effect2) {
      super(properties);
      this.effect1 = effect1;
      this.effect2 = effect2;
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         double rand = Math.random();
         if (ModList.get().isLoaded("neapolitan")) {
            if (rand < 0.7) {
               pLivingEntity.addEffect(new MobEffectInstance(
                       getCompatEffect("neapolitan", new ResourceLocation(
                               "neapolitan", "berserking")).get(), 100, 0));
            }
            if (rand > 0.3) {
               pLivingEntity.addEffect(new MobEffectInstance(
                       getCompatEffect("neapolitan", new ResourceLocation(
                               "neapolitan", "sugar_rush")).get(), 140, 0));
               extendEffect(pLivingEntity);
            }
         }
      }
      return pStack;
   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }

   public void extendEffect( LivingEntity player) {
      Iterator<MobEffectInstance> effects = player.getActiveEffects().iterator();
      while (effects.hasNext()) {
         MobEffectInstance effect = effects.next();
         double rand = Math.random();
         if (effect != null && effect.getDuration() > 10 && effect.getDescriptionId().equals(effect1) || effect.getDescriptionId().equals(effect2)) {
            if (rand < 0.4) {
               player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() + 240, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            } else if (rand > Math.min(0.7, (double) 1 / (effect.getAmplifier() + 1))) {
               player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
         }
      }
   }


   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      if (!CopperPotConfig.CLIENT.tooltips.get()) return;
      MutableComponent tip = Component.translatable("copperpot.tooltip.mint_meringue.tip");
      MutableComponent tip2 = Component.translatable("copperpot.tooltip.mint_meringue.tip2");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.BLUE));
      pTooltipComponents.add(tip2.withStyle(ChatFormatting.BLUE));
   }

}
