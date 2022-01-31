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

import net.minecraft.item.Item.Properties;

public class GuardianSouffle extends Item {

    public GuardianSouffle(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!worldIn.isClientSide && entityLiving.isInWaterRainOrBubble()) {
            float negatives = 0.0F;
            for (EffectInstance effect : entityLiving.getActiveEffects()) {
                if (effect.getEffect().getCategory() == EffectType.HARMFUL) {
                    negatives++;
                }
            }
            entityLiving.heal(negatives * 2F);
        }
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent tip = TextUtils.getTranslation("tooltip.guardian_souffle.tip");
        IFormattableTextComponent tip2 = TextUtils.getTranslation("tooltip.guardian_souffle.tip2");
        tooltip.add(tip.withStyle(TextFormatting.BLUE));
        tooltip.add(tip2.withStyle(TextFormatting.BLUE));
    }
}
