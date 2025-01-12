package com.davigj.copperpot.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CarrotCupcake extends Item {
   String effectName;

   public CarrotCupcake(Properties properties, String effectName) {
      super(properties);
      this.effectName = effectName;
   }


   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         intensify(pLivingEntity);
      }
      return pStack;
   }

   public void intensify (LivingEntity player) {
       for (MobEffectInstance effect : player.getActiveEffects()) {
           if (effect.getDuration() > 10 && effect.getDescriptionId().equals(effectName) && Math.random() > 0.2 * (effect.getAmplifier() + 1)) {
               player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(),
                       effect.getAmplifier() + 1, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
           }
       }
   }

}

