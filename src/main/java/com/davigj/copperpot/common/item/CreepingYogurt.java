package com.davigj.copperpot.common.item;

import com.davigj.copperpot.CopperPotConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CreepingYogurt extends Item {
   public CreepingYogurt(Item.Properties properties) {
      super(properties);
   }
   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide() && pLivingEntity instanceof Player) {
         // TODO: Check how slabfish_snacks works--does it emulate the slabfish actually *using* the item
         pewpew(pLivingEntity, pLevel);
      }
      if (pStack.isEmpty()) {
         return new ItemStack(Items.BOWL);
      } else {
         ItemStack itemstack = new ItemStack(Items.BOWL);
         Player playerentity = (Player) pLivingEntity;
         if (!playerentity.getInventory().add(itemstack)) {
            playerentity.drop(itemstack, false);
         }
      }
      return pStack;
   }

   public void pewpew(LivingEntity player, Level worldIn) {
      BlockPos pos = player.blockPosition();
      Iterator<MobEffectInstance> effects = player.getActiveEffects().iterator();
      Direction direction = player.getDirection();
      ArrayList<MobEffectInstance> storedEffects = new ArrayList<>();
      while (effects.hasNext()) {
         MobEffectInstance effect = effects.next();
         if (effect.getDuration() > 10) {
            storedEffects.add(effect);
         }
      }
      int xpMult = 0;
      AreaEffectCloud dummy = new AreaEffectCloud(worldIn, pos.getX(), pos.getY(), pos.getZ());
      for (MobEffectInstance storedEffect : storedEffects) {
         AreaEffectCloud steam = new AreaEffectCloud(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

         steam.setDuration(200);
         steam.setRadius(0.2F);
         steam.addEffect(new MobEffectInstance(storedEffect.getEffect(), (int) ((storedEffect.getDuration()) * 0.6), storedEffect.getAmplifier()));
         for (LivingEntity living : steam.level().getEntitiesOfClass(LivingEntity.class, steam.getBoundingBox().inflate(2.0D, 1.0D, 2.0D))) {
            living.addEffect(new MobEffectInstance(storedEffect.getEffect(), (int) ((storedEffect.getDuration()) * 0.6), storedEffect.getAmplifier()));
         }
         worldIn.addFreshEntity(steam);
         xpMult++;
      }
      double resist = 0;
//         TODO: Find out how to implement SR explosive damage reduction
//        if (ModList.get().isLoaded("savage_and_ravage")) {
//            resist = (double)player.getAttributeValue(SRAttributes.EXPLOSIVE_DAMAGE_REDUCTION.get());
//            LOGGER.debug(resist);
//        }
      resist = Math.max(resist, player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

      player.push((1.8 * (1.0D - resist)) * direction.getStepX() + (0.2 * xpMult),
              0.8, (1.8 * (1.0D - resist)) * direction.getStepZ() + (0.2 * xpMult));
      worldIn.explode(dummy, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.6F, false, Level.ExplosionInteraction.NONE);
      player.removeAllEffects();
   }

   @Override
   public SoundEvent getEatingSound() {
      return SoundEvents.CREEPER_PRIMED;
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      if (!CopperPotConfig.CLIENT.tooltips.get()) return;
      MutableComponent tip = Component.translatable("copperpot.tooltip.creeping_yogurt.tip");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.GREEN));
   }
}
