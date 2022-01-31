package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.Iterator;

public class SingleVanillaAdd extends Item {
    String effectName;

    public SingleVanillaAdd(Item.Properties properties, String effect) {
        super(properties);
        this.effectName = effect;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!worldIn.isClientSide) {
            extendEffect(entityLiving);
        }
        return stack;
    }

    public void extendEffect(LivingEntity player) {
        Iterator effects = player.getActiveEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && effect.getDescriptionId().equals(effectName)) {
                player.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration() + 200,
                        effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
        }
    }
}
