package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeasonalAgar extends Item {

    ArrayList<String> effectList;
    public SeasonalAgar(Item.Properties properties, ArrayList<String> effects) {
        super(properties);
        this.effectList = effects;
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
            for(String i : effectList) {
                if (effect.getDuration() > 10 && effect.getEffectName().equals(i)) {
                    player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() +
                            durationBonus, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
                }
            }
        }
    }

    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent prefix = TextUtils.getTranslation("tooltip.seasonal_agar.prefix", new Object[0]);
        tooltip.add(prefix.mergeStyle(TextFormatting.BLUE));
        StringTextComponent effectDescription;
        for(Iterator var6 = effectList.iterator(); var6.hasNext(); tooltip.add(effectDescription.mergeStyle(TextFormatting.AQUA))) {
            effectDescription = new StringTextComponent("");
            IFormattableTextComponent effectName = new TranslationTextComponent((String) var6.next());
            effectDescription.append(effectName);
        }
    }
}
