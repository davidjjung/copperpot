package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.Iterator;

public class CarrotCupcake extends Item {
    String effectName;

    public CarrotCupcake(Properties properties, String effectName) {
        super(properties);
        this.effectName = effectName;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            intensify(entityLiving);
        }
        return stack;
    }

    public void intensify (LivingEntity player) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && effect.getEffectName().equals(effectName) && Math.random() > 0.2 * (effect.getAmplifier() + 1)) {
                player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration(),
                        effect.getAmplifier() + 1, effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
            }
        }
    }
}
