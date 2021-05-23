package com.davigj.copperpot.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.function.Supplier;

public class AdzukiPaste extends Item {
    public static final Logger LOGGER = LogManager.getLogger();
    String effectName;

    public AdzukiPaste(Item.Properties properties, String effect) {
        super(properties);
        this.effectName = effect;
    }

    @Override
    public ItemStack onItemUseFinish (ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
            serverplayerentity.addStat(Stats.ITEM_USED.get(this));
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            double rand = Math.random();
            if (entityLiving instanceof PlayerEntity && !((PlayerEntity)entityLiving).abilities.isCreativeMode) {
                if(ModList.get().isLoaded("neapolitan")) {
                    if (rand < 0.7) {
                        entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(
                                getCompatEffect("neapolitan", new ResourceLocation(
                                        "neapolitan", "harmony")).get(), 40)));
                    }
                    if (rand > 0.2) {
                        extendEffect(entityLiving);
                    }
                }
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity playerentity = (PlayerEntity)entityLiving;
                if (!playerentity.inventory.addItemStackToInventory(itemstack)) {
                    playerentity.dropItem(itemstack, false);
                }
            }
            if(ModList.get().isLoaded("neapolitan")) {
                if (rand < 0.7) {
                    entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(
                            getCompatEffect("neapolitan", new ResourceLocation(
                                    "neapolitan", "harmony")).get(), 80)));
                }
                if (rand > 0.2) {
                    extendEffect(entityLiving);
                }
            }
            return stack;
        }
    }

    private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }

    public void extendEffect(LivingEntity player) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && effect.getEffectName().equals(effectName)) {
                player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() + 40, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
            }
        }
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
    }
}
