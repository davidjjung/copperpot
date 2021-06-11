package com.davigj.copperpot.common.items;

import com.minecraftabnormals.savageandravage.common.entity.CreepieEntity;
import com.minecraftabnormals.savageandravage.core.registry.SREntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.Iterator;

public class Sourdough extends Item {
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

    public void hissss (LivingEntity player, World worldIn, ItemStack stack) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && ModList.get().isLoaded("savageandravage")) {
                CreepieEntity effectie = (CreepieEntity)((EntityType) SREntities.CREEPIE.get()).create(worldIn);
                if (effectie != null) {
                    if (stack.hasDisplayName()) {
                        effectie.setCustomName(stack.getDisplayName());
                    }
                    effectie.copyLocationAndAnglesFrom(player);
                    effectie.attackPlayersOnly = false;
                    effectie.setOwnerId(player.getUniqueID());
                    worldIn.addEntity(effectie);
                    effectie.addPotionEffect(new EffectInstance(effect.getPotion(), (int)(effect.getDuration() * 0.6)));
                }
            }
        }
        player.clearActivePotions();
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_CREEPER_PRIMED;
    }

}
