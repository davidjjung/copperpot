package com.davigj.copperpot.common.item;

import com.davigj.copperpot.CopperPotConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SeasonalAgarItem extends Item {
    TagKey<MobEffect> effects;

    public SeasonalAgarItem(Properties pProperties, TagKey<MobEffect> effects) {
        super(pProperties);
        this.effects = effects;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
        super.finishUsingItem(pStack, pLevel, pLivingEntity);
        if (!pLevel.isClientSide()) {
            extendEffect(pLivingEntity);
        }
        return pStack;
    }

    public void extendEffect(LivingEntity player) {
        Collection<MobEffectInstance> effectList = player.getActiveEffects();
        for (MobEffectInstance inst: effectList) {
            ITag<MobEffect> tag = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags()).getTag(this.effects);
            if (tag.contains(inst.getEffect()) && inst.getDuration() > 10) {
                MobEffectInstance extended = new MobEffectInstance(inst.getEffect(), inst.getDuration() + player.getRandom().nextInt(100) + 100, inst.getAmplifier());
                player.addEffect(extended);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
        if (!CopperPotConfig.CLIENT.tooltips.get()) return;
        MutableComponent prefix = Component.translatable("copperpot.tooltip.seasonal_agar.prefix");
        pTooltipComponents.add(prefix.withStyle(ChatFormatting.GRAY));
        ITag<MobEffect> tag = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags()).getTag(this.effects);
        List<MobEffect> list = tag.stream().toList();
        for (MobEffect effect : list) {
            MutableComponent effectDescription = Component.literal("");
            MutableComponent effectName = Component.translatable(effect.getDescriptionId());
            effectDescription.append(effectName);
            pTooltipComponents.add(effectDescription.withStyle(ChatFormatting.AQUA));
        }
    }
}
