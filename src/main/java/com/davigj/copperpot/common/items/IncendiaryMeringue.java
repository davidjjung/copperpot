package com.davigj.copperpot.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.Iterator;

import net.minecraft.item.Item.Properties;

public class IncendiaryMeringue extends Item {
    public IncendiaryMeringue(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!worldIn.isClientSide) {
            double rand = Math.random();
            if (ModList.get().isLoaded("atmospheric") && ModList.get().isLoaded("neapolitan")) {
                if (rand < 0.5) {
                    entityLiving.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 100));
                }
                if (rand > 0.3) {
                    setAblaze(entityLiving);
                }
            }
        }
        return stack;
    }

    public void setAblaze(LivingEntity player) {
        player.setSecondsOnFire(10);
        Iterator effects = player.getActiveEffects().iterator();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            double rand = Math.random();
            if (effect != null && effect.getDuration() > 10 && effect.getDescriptionId().equals("effect.minecraft.strength")) {
                player.setHealth(player.getHealth() - (float)(effect.getAmplifier() * 2));
                if (rand < (double)((1 / (effect.getAmplifier() + 1)) + 0.2D)) {
                player.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                }
            }
        }
    }
}
