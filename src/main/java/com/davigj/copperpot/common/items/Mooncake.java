package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class Mooncake extends Item {
    public Mooncake(Properties properties) {
        super(properties);
    }

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote && worldIn.getDimensionType().doesBedWork()) {
            moonlight(entityLiving, worldIn);
        } else if (!worldIn.getDimensionType().doesBedWork()) {
            entityLiving.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
            entityLiving.addPotionEffect(new EffectInstance(Effects.POISON, 480));
        }
        return stack;
    }

    private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }

    public void moonlight(LivingEntity entity, World worldIn) {
        long daytime = worldIn.getDayTime() % 24000;
        long moonProxTime = Math.abs(daytime - 6000);
        double moon = Math.abs(4 - worldIn.getMoonPhase());
        if (moonProxTime > 12000) {
            moonProxTime = Math.abs(24000 - moonProxTime);
        }
        if (worldIn.isDaytime()) {
            moon = 0.7;
        } else {
            moon = 1 + (0.1 * moon);
        }
//        LOGGER.debug("daytime: " + daytime + " moon multiplier: " + moon + " moon prox time: " + moonProxTime);
        if (ModList.get().isLoaded("neapolitan")) {
            entity.addPotionEffect(new EffectInstance(new EffectInstance(
                    getCompatEffect("neapolitan", new ResourceLocation(
                            "neapolitan", "harmony")).get(), (int) ((0.11 * moonProxTime) * moon))));
        }
    }
}
