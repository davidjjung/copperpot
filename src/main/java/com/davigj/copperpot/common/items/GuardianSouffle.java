package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;

import java.util.Iterator;

public class GuardianSouffle extends Item {

    public GuardianSouffle(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote && entityLiving.isInWaterRainOrBubbleColumn()) {
            float negatives = 0.0F;
            for (EffectInstance effect : entityLiving.getActivePotionEffects()) {
                if (effect.getPotion().getEffectType() == EffectType.HARMFUL) {
                    negatives++;
                }
            }
            entityLiving.heal(negatives * 2F);
        }
        return stack;
    }
}
