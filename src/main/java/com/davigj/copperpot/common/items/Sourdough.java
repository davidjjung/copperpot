package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;


import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class Sourdough extends Item {
    public static final ResourceLocation CREEPIE = new ResourceLocation("savageandravage", "creepie");

    public Sourdough(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            hissss(entityLiving, worldIn, stack);
        }
        return stack;
    }

    public void hissss(LivingEntity player, World worldIn, ItemStack stack) {
        for (EffectInstance effect : player.getActivePotionEffects()) {
            if (effect.getDuration() > 10 && ModList.get().isLoaded("savageandravage")) {
                ResourceLocation creepieType = ForgeRegistries.ENTITIES.getValue(CREEPIE).getRegistryName();
                BlockPos pos = player.getPosition();
                ServerWorld server = worldIn.getServer().getWorld(worldIn.getDimensionKey());
                CompoundNBT nbt = new CompoundNBT();
                Entity effectcreepie = summonEntity(creepieType, pos, nbt, server, player);
                if (stack.hasDisplayName()) {
                    effectcreepie.setCustomName(stack.getDisplayName());
                }
                worldIn.addEntity(effectcreepie);
                ((LivingEntity) effectcreepie).addPotionEffect(new EffectInstance(effect.getPotion(), (int) (effect.getDuration() * 0.6)));
            }
        }
        player.clearActivePotions();
    }

    private static Entity summonEntity(ResourceLocation type, BlockPos pos, CompoundNBT nbt, ServerWorld server, LivingEntity owner) {
        CompoundNBT compoundnbt = nbt.copy();
        compoundnbt.putString("id", type.toString());
        compoundnbt.putUniqueId("OwnerUUID", owner.getUniqueID());
        compoundnbt.putByte("ExplosionRadius", (byte)((int)1.2F));
        Entity entity = EntityType.loadEntityAndExecute(compoundnbt, server, (p_218914_1_) -> {
            p_218914_1_.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), p_218914_1_.rotationYaw, p_218914_1_.rotationPitch);

            return p_218914_1_;
        });
        return entity;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_CREEPER_PRIMED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent tip = TextUtils.getTranslation("tooltip.sourdough.tip");
        tooltip.add(tip.mergeStyle(TextFormatting.GREEN));
    }
}
