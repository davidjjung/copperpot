package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class SpicedAppleJam extends Item {
    String effectName;
    String effectName2;

    public SpicedAppleJam(Item.Properties properties, String effect1, String effect2) {
        super(properties);
        this.effectName = effect1;
        this.effectName2 = effect2;
    }

    @Override
    public ItemStack finishUsingItem (ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
            serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            double rand = Math.random();
            if (entityLiving instanceof PlayerEntity && !((PlayerEntity)entityLiving).abilities.instabuild) {
                if(ModList.get().isLoaded("fruitful")) {
                    if (rand < 0.7) {
                        entityLiving.addEffect(new EffectInstance(
                                getCompatEffect("fruitful", new ResourceLocation(
                                        "fruitful", "sustaining")).get(), 200, 0));
                    }
                    extendEffect(entityLiving);
                }
                if(ModList.get().isLoaded("abundance")) {
                    if (rand > 0.3) {
                        entityLiving.addEffect(new EffectInstance(new EffectInstance(
                                getCompatEffect("abundance", new ResourceLocation(
                                        "abundance", "supportive")).get(), 200, 0)));
                    }
                    if (rand > 0.3) {
                        extendEffect(entityLiving);
                    }
                }
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity playerentity = (PlayerEntity)entityLiving;
                if (!playerentity.inventory.add(itemstack)) {
                    playerentity.drop(itemstack, false);
                }
            }
            if(ModList.get().isLoaded("fruitful")) {
                if (rand < 0.7) {
                    entityLiving.addEffect(new EffectInstance(
                            getCompatEffect("fruitful", new ResourceLocation(
                                    "fruitful", "sustaining")).get(), 100, 0));
                }
                extendEffect(entityLiving);
            }
            if(ModList.get().isLoaded("abundance")) {
                if (rand > 0.3) {
                    entityLiving.addEffect(new EffectInstance(new EffectInstance(
                            getCompatEffect("abundance", new ResourceLocation(
                                    "abundance", "supportive")).get(), 100, 0)));
                }
                if (rand > 0.3) {
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
        Iterator effects = player.getActiveEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && effect.getDescriptionId().equals(effectName) || effect.getDescriptionId().equals(effectName2)) {
                player.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration() + 60, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
        }
    }

    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return DrinkHelper.useDrink(worldIn, playerIn, handIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent tip = TextUtils.getTranslation("tooltip.spiced_apple_jam.tip");
        IFormattableTextComponent tip2 = TextUtils.getTranslation("tooltip.spiced_apple_jam.tip2");
        tooltip.add(tip.withStyle(TextFormatting.BLUE));
        tooltip.add(tip2.withStyle(TextFormatting.BLUE));
    }
}
