package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class CarrotCupcake extends Item {
    String effectName;

    public CarrotCupcake(Properties properties, String effectName) {
        super(properties);
        this.effectName = effectName;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!worldIn.isClientSide) {
            intensify(entityLiving);
        }
        return stack;
    }

    public void intensify (LivingEntity player) {
        Iterator effects = player.getActiveEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10 && effect.getDescriptionId().equals(effectName) && Math.random() > 0.2 * (effect.getAmplifier() + 1)) {
                player.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration(),
                        effect.getAmplifier() + 1, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent tip = TextUtils.getTranslation("tooltip.carrot_cupcake.tip");
        tooltip.add(tip.withStyle(TextFormatting.BLUE));
    }
}
