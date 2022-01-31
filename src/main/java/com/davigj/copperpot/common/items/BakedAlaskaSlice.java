package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class BakedAlaskaSlice extends Item {
    public static final Logger LOGGER = LogManager.getLogger();
    public BakedAlaskaSlice(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity livingEntity) {
        if (!worldIn.isClientSide && ModList.get().isLoaded("neapolitan")) {
            double random = Math.random();
            if (random < 0.25) {
                livingEntity.addEffect(new EffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "sugar_rush")).get(), 200, 2));
            } else if (random < 0.5 && random > 0.25) {
                livingEntity.addEffect(new EffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "vanilla_scent")).get(), 100));
            } else if (random > 0.5 && random < 0.8) {
                livingEntity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200));
            } else {
                livingEntity.heal(4.0F);
            }
        }
        return super.finishUsingItem(stack, worldIn, livingEntity);
    }

    private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }
}
