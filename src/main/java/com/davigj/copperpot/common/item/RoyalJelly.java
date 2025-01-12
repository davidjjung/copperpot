package com.davigj.copperpot.common.item;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.CopperPotConfig;
import com.davigj.copperpot.core.registry.CPItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.MathUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class RoyalJelly extends Item {
   private static final List<MobEffectInstance> EFFECTS;

   public RoyalJelly(Item.Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         fliparoo(pLivingEntity);
      }
      return pStack;
   }


   public void fliparoo(LivingEntity entity) {
      Iterator<MobEffectInstance> it = entity.getActiveEffects().iterator();
      while (it.hasNext()) {
         MobEffectInstance effect = it.next();
         if (effect.getDuration() > 10 && effect.getDescriptionId().equals("effect.minecraft.poison")) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, (int) (effect.getDuration() * 0.33),
                    Math.max(effect.getAmplifier() - 2, 0), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
         }
      }
      entity.removeEffect(MobEffects.POISON);
   }


   @Override
   public InteractionResult interactLivingEntity( ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      if (target instanceof Bee bee) {
         if (bee.isAlive() || bee.hasStung()) {
            return InteractionResult.SUCCESS;
         }
      }
      return InteractionResult.PASS;
   }

   static {
      EFFECTS = List.of(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1),
              new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1),
              new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0));
   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      if (!CopperPotConfig.CLIENT.tooltips.get()) return;
      MutableComponent textWhenFeeding = Component.translatable("copperpot.tooltip.royal_jelly.when_feeding");
      MutableComponent textWhenEating = Component.translatable("copperpot.tooltip.royal_jelly.when_eating");
      pTooltipComponents.add(textWhenEating.withStyle(ChatFormatting.BLUE));
      pTooltipComponents.add(textWhenFeeding.withStyle(ChatFormatting.GRAY));


      MutableComponent effectDescription;
      MobEffect effect;
      for ( Iterator<MobEffectInstance> var6 = EFFECTS.iterator(); var6.hasNext(); pTooltipComponents.add(effectDescription.withStyle(effect.getCategory().getTooltipFormatting()))) {
         MobEffectInstance effectInstance = var6.next();
         effectDescription = Component.literal("");
         MutableComponent effectName = Component.translatable(effectInstance.getDescriptionId());
         effectDescription.append(effectName);
         effect = effectInstance.getEffect();
         if (effectInstance.getAmplifier() > 0) {
            effectDescription.append(" ").append(Component.translatable("potion.potency." + effectInstance.getAmplifier()));
         }

         if (effectInstance.getDuration() > 20) {
            effectDescription.append(" (").append(MobEffectUtil.formatDuration(effectInstance, 1.0F)).append(")");
         }
      }
      MutableComponent textWhenBeeing = Component.translatable("copperpot.tooltip.royal_jelly.fix_bee");
      pTooltipComponents.add(textWhenBeeing.withStyle(ChatFormatting.BLUE));
   }


   @Mod.EventBusSubscriber(
           modid = CopperPot.MODID,
           bus = Mod.EventBusSubscriber.Bus.FORGE
   )
   public static class beeFeedEvent {
      public beeFeedEvent() {
      }

      @SubscribeEvent
      public static void onRoyalJellyApplied( PlayerInteractEvent.EntityInteract event) {
         Player player = event.getEntity();
         Entity target = event.getTarget();
         ItemStack itemStack = event.getItemStack();
         if (target instanceof Bee bee) {
            if (bee.isAlive() && itemStack.getItem().equals(CPItems.ROYAL_JELLY.get())) {
               bee.setHealth(bee.getMaxHealth());
               bee.setHasStung(false);
               if (bee.isBaby()) {
                  bee.ageUp(120, true);
               }

               beeFx(bee);

               bee.setRemainingPersistentAngerTime(0);

               bee.level().playSound((Player) null, target.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, 1.4F);

               for (int i = 0; i < 5; ++i) {
                  double d0 = MathUtils.RAND.nextGaussian() * 0.02D;
                  double d1 = MathUtils.RAND.nextGaussian() * 0.02D;
                  double d2 = MathUtils.RAND.nextGaussian() * 0.02D;
                  bee.level().addParticle(ParticleTypes.FALLING_HONEY, bee.getRandomX(1.0D), bee.getRandomY() + 0.5D, bee.getRandomZ(1.0D), d0, d1, d2);
               }

               if (!player.isCreative()) {
                  itemStack.shrink(1);
               }

               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
            }
         }
      }

      private static void beeFx(Bee entity) {
         List<MobEffectInstance> list = RoyalJelly.EFFECTS;
         Iterator<MobEffectInstance> var7 = list.iterator();
         while (var7.hasNext()) {
            MobEffectInstance effect = var7.next();
            entity.addEffect(new MobEffectInstance(effect));
         }
      }

   }


}
