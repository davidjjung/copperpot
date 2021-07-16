package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.Iterator;

public class IncendiaryMeringue extends Item {
    public IncendiaryMeringue(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            double rand = Math.random();
            if (ModList.get().isLoaded("atmospheric") && ModList.get().isLoaded("neapolitan")) {
                if (rand < 0.5) {
                    entityLiving.addPotionEffect(new EffectInstance(Effects.STRENGTH, 100));
                }
                if (rand > 0.3) {
                    setAblaze(entityLiving);
                }
            }
        }
        return stack;
    }

    public void setAblaze(LivingEntity player) {
        player.setFire(10);
        Iterator effects = player.getActivePotionEffects().iterator();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            double rand = Math.random();
            if (effect != null && effect.getDuration() > 10 && effect.getEffectName().equals("effect.minecraft.strength")) {
                player.setHealth(player.getHealth() - (float)(effect.getAmplifier() * 2));
                if (rand < (double)((1 / (effect.getAmplifier() + 1)) + 0.2D)) {
                player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
                }
            }
        }
    }
}
