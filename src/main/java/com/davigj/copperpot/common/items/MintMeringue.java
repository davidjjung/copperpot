package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.function.Supplier;

public class MintMeringue extends Item {
    String effect1;
    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);
    String effect2;

    //TODO: Make the likelihood of effect intensification scale with the level of effect--the higher the easier
    public MintMeringue(Item.Properties properties, String effect1, String effect2) {
        super(properties);
        this.effect1 = effect1;
        this.effect2 = effect2;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            double rand = Math.random();
            if (ModList.get().isLoaded("neapolitan")) {
                if (rand < 0.7) {
                    entityLiving.addPotionEffect(new EffectInstance(
                            getCompatEffect("neapolitan", new ResourceLocation(
                                    "neapolitan", "berserking")).get(), 100, 0));
                }
                if (rand > 0.3) {
                    entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(
                            getCompatEffect("neapolitan", new ResourceLocation(
                                    "neapolitan", "sugar_rush")).get(), 140, 0)));
                    extendEffect(entityLiving);
                }
            }
        }
        return stack;
    }

    private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }

    public void extendEffect(LivingEntity player) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            double rand = Math.random();
            if (effect != null && effect.getDuration() > 10 && effect.getEffectName().equals(effect1) || effect.getEffectName().equals(effect2)) {
                if (rand < 0.4) {
                    player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() + 240, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
                } else if (rand > Math.min(0.7, 1 / (effect.getAmplifier() + 1))) {
                    player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
                }
            }
        }
    }
}
