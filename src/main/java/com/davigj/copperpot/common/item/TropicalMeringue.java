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

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class TropicalMeringue extends Item {
   String effect1;
   String effect2;

   public TropicalMeringue(Properties properties, String effect1, String effect2) {
      super(properties);
      this.effect1 = effect1;
      this.effect2 = effect2;
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         double rand = Math.random();
         if (ModList.get().isLoaded("atmospheric") && ModList.get().isLoaded("neapolitan")) {
            if (rand < 0.7) {
               pLivingEntity.addEffect(new MobEffectInstance(
                       getCompatEffect("atmospheric", new ResourceLocation(
                               "atmospheric", "spitting")).get(), 60, 0));
               pLivingEntity.addEffect(new MobEffectInstance(
                       getCompatEffect("neapolitan", new ResourceLocation(
                               "neapolitan", "agility")).get(), 100, 0));
            }
            if (rand > 0.15) {
               intensify(pLivingEntity);
            }
         }
      }
      return pStack;   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }

   public void intensify(LivingEntity player) {
       for (MobEffectInstance effect : player.getActiveEffects()) {
           double rand = Math.random();
           if (effect != null && effect.getDuration() > 10 && effect.getDescriptionId().equals(effect1) || Objects.requireNonNull(effect).getDescriptionId().equals(effect2)) {
               if (rand < 0.7) {
                   player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() + 80, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
               }
               if (effect.getDescriptionId().equals(effect1) && rand > Math.min(0.5, (float) 1 / (effect.getAmplifier() + 1))) {
                   player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
               }
           }
       }
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      if (!CopperPotConfig.CLIENT.tooltips.get()) return;
      MutableComponent tip = Component.translatable("copperpot.tooltip.tropical_meringue.tip");
      MutableComponent tip2 = Component.translatable("copperpot.tooltip.tropical_meringue.tip2");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.BLUE));
      pTooltipComponents.add(tip2.withStyle(ChatFormatting.BLUE));
   }
}
