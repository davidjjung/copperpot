package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.Iterator;

public class SeasonalAgar extends Item {
    String effect1;
    String effect2;
    String effect3;
    String effect4;

    public SeasonalAgar(Item.Properties properties, String effect1, String effect2, String effect3, String effect4) {
        super(properties);
        this.effect1 = effect1;
        this.effect2 = effect2;
        this.effect3 = effect3;
        this.effect4 = effect4;
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
            double rand = Math.random();
            int durationBonus;
            if (rand < 0.7) {
                durationBonus = 200;
            } else { durationBonus = 400; }
            if (effect.getDuration() > 10 && effect.getEffectName().equals(effect1) || effect.getEffectName().equals(effect2)
            || effect.getEffectName().equals(effect3) || effect.getEffectName().equals(effect4)) {
                player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() + durationBonus, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
            }
        }
    }
}
