package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

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
        Iterator effects = player.getActivePotionEffects().iterator();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            if (effect.getDuration() > 10 && ModList.get().isLoaded("savageandravage")) {
                ResourceLocation creepieType = ForgeRegistries.ENTITIES.getValue(CREEPIE).getRegistryName();
                BlockPos pos = player.getPosition();
                ServerWorld server = worldIn.getServer().getWorld(worldIn.getDimensionKey());
                CompoundNBT nbt = new CompoundNBT();
                Entity effectcreepie = summonEntity(creepieType, pos, nbt, server, player);
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

}
