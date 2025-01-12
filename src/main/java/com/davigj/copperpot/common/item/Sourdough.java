package com.davigj.copperpot.common.item;

import com.davigj.copperpot.CopperPotConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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

public class Sourdough extends Item {
   public static final ResourceLocation CREEPIE = new ResourceLocation("savage_and_ravage", "creepie");

   public Sourdough(Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         hissss(pLivingEntity, pLevel, pStack);
      }
      return pStack;   }


   public void hissss(LivingEntity player, Level worldIn, ItemStack stack) {
      for ( MobEffectInstance effect : player.getActiveEffects()) {
         if (effect.getDuration() > 10 && ModList.get().isLoaded("savage_and_ravage")) {
            EntityType<?> creepieType = ForgeRegistries.ENTITY_TYPES.getValue(CREEPIE);
            BlockPos pos = player.blockPosition();
            ServerLevel server = worldIn.getServer().getLevel(worldIn.dimension());
            CompoundTag nbt = new CompoundTag();
            Entity effectcreepie = summonEntity(creepieType, pos, nbt, server, player);
            if (stack.hasCustomHoverName()) {
               effectcreepie.setCustomName(stack.getHoverName());
            }
            worldIn.addFreshEntity(effectcreepie);
            ((LivingEntity) effectcreepie).addEffect(new MobEffectInstance(effect.getEffect(), (int) (effect.getDuration() * 0.6)));
         }
      }
      player.removeAllEffects();
   }

   private static Entity summonEntity( EntityType<?> type, BlockPos pos, CompoundTag nbt, ServerLevel server, LivingEntity owner) {
      CompoundTag compoundnbt = nbt.copy();
      compoundnbt.putString("id", type.toString());
      compoundnbt.putUUID("OwnerUUID", owner.getUUID());
      compoundnbt.putByte("ExplosionRadius", (byte)((int)1.2F));
      Entity entity = type.create(server, compoundnbt, null, pos, MobSpawnType.MOB_SUMMONED, true, false );
        if (entity != null) {
             entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), entity.getYRot(), entity.getXRot());
        }
      return entity;
   }
   @Override
   public SoundEvent getEatingSound() {
      return SoundEvents.CREEPER_PRIMED;
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      if (!CopperPotConfig.CLIENT.tooltips.get()) return;
      MutableComponent tip = Component.translatable("copperpot.tooltip.sourdough.tip");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.GREEN));
   }
}
