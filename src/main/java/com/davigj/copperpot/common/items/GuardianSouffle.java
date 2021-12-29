package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class GuardianSouffle extends Item {

    public GuardianSouffle(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote && entityLiving.isInWaterRainOrBubbleColumn()) {
            float negatives = 0.0F;
            for (EffectInstance effect : entityLiving.getActivePotionEffects()) {
                if (effect.getPotion().getEffectType() == EffectType.HARMFUL) {
                    negatives++;
                }
            }
            entityLiving.heal(negatives * 2F);
        }
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent tip = TextUtils.getTranslation("tooltip.guardian_souffle.tip");
        IFormattableTextComponent tip2 = TextUtils.getTranslation("tooltip.guardian_souffle.tip2");
        tooltip.add(tip.mergeStyle(TextFormatting.BLUE));
        tooltip.add(tip2.mergeStyle(TextFormatting.BLUE));
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GUARDIAN_AMBIENT;
    }
}
