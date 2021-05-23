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
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            extendEffect(entityLiving);
        }
        return stack;
    }

    public void extendEffect(LivingEntity player) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && effect.getEffectName().equals(effectName)) {
                player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() + 80,
                        effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
            }
        }
    }
}
