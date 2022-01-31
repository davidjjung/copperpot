package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.registry.CopperPotItems;
import com.davigj.copperpot.core.utils.TextUtils;
import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class RoyalJelly extends Item {
    private static final List<EffectInstance> EFFECTS;

    public RoyalJelly(Item.Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent textWhenFeeding = TextUtils.getTranslation("tooltip.royal_jelly.when_feeding", new Object[0]);
        IFormattableTextComponent textWhenEating = TextUtils.getTranslation("tooltip.royal_jelly.when_eating", new Object[0]);
        tooltip.add(textWhenEating.withStyle(TextFormatting.BLUE));
        tooltip.add(textWhenFeeding.withStyle(TextFormatting.GRAY));

        StringTextComponent effectDescription;
        Effect effect;
        for (Iterator var6 = EFFECTS.iterator(); var6.hasNext(); tooltip.add(effectDescription.withStyle(effect.getCategory().getTooltipFormatting()))) {
            EffectInstance effectInstance = (EffectInstance) var6.next();
            effectDescription = new StringTextComponent(" ");
            IFormattableTextComponent effectName = new TranslationTextComponent(effectInstance.getDescriptionId());
            effectDescription.append(effectName);
            effect = effectInstance.getEffect();
            if (effectInstance.getAmplifier() > 0) {
                effectDescription.append(" ").append(new TranslationTextComponent("potion.potency." + effectInstance.getAmplifier()));
            }

            if (effectInstance.getDuration() > 20) {
                effectDescription.append(" (").append(EffectUtils.formatDuration(effectInstance, 1.0F)).append(")");
            }
        }
        IFormattableTextComponent textWhenBeeing = TextUtils.getTranslation("tooltip.royal_jelly.fix_bee", new Object[0]);
        tooltip.add(textWhenBeeing.withStyle(TextFormatting.BLUE));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!worldIn.isClientSide) {
            fliparoo(entityLiving);
        }
        return stack;
    }

    public void fliparoo(LivingEntity entity) {
        Iterator<EffectInstance> it = entity.getActiveEffects().iterator();
        while (it.hasNext()) {
            EffectInstance effect = it.next();
            if (effect.getDuration() > 10 && effect.getDescriptionId().equals("effect.minecraft.poison")) {
                entity.addEffect(new EffectInstance(Effects.REGENERATION, (int) (effect.getDuration() * 0.33),
                        Math.max(effect.getAmplifier() - 2, 0), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
        }
        entity.removeEffect(Effects.POISON);
    }

    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (target instanceof BeeEntity) {
            BeeEntity bee = (BeeEntity) target;
            if (bee.isAlive() || bee.hasStung()) {
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    static {
        EFFECTS = Lists.newArrayList(new EffectInstance(Effects.ABSORPTION, 1200, 1),
                new EffectInstance(Effects.DAMAGE_RESISTANCE, 1200, 1),
                new EffectInstance(Effects.MOVEMENT_SPEED, 1200, 0));
    }

    private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }

    @Mod.EventBusSubscriber(
            modid = "copperpot",
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static class beeFeedEvent {
        public beeFeedEvent() {
        }

        @SubscribeEvent
        public static void onRoyalJellyApplied(PlayerInteractEvent.EntityInteract event) {
            PlayerEntity player = event.getPlayer();
            Entity target = event.getTarget();
            ItemStack itemStack = event.getItemStack();
            if (target instanceof BeeEntity) {
                BeeEntity entity = (BeeEntity) target;
                if (entity.isAlive() && itemStack.getItem().equals(CopperPotItems.ROYAL_JELLY.get())) {
                    entity.setHealth(entity.getMaxHealth());
                    entity.setHasStung(false);
                    if (entity.isBaby()) {
                        entity.ageUp(120, true);
                    }

                    beeFx(entity);

                    entity.setRemainingPersistentAngerTime(0);

                    entity.level.playSound((PlayerEntity) null, target.blockPosition(), SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, 1.4F);

                    for (int i = 0; i < 5; ++i) {
                        double d0 = MathUtils.RAND.nextGaussian() * 0.02D;
                        double d1 = MathUtils.RAND.nextGaussian() * 0.02D;
                        double d2 = MathUtils.RAND.nextGaussian() * 0.02D;
                        entity.level.addParticle((IParticleData) ParticleTypes.FALLING_HONEY, entity.getRandomX(1.0D), entity.getRandomY() + 0.5D, entity.getRandomZ(1.0D), d0, d1, d2);
                    }

                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }

                    event.setCancellationResult(ActionResultType.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }

        private static void beeFx(BeeEntity entity) {
            List<EffectInstance> list = RoyalJelly.EFFECTS;
            Iterator<EffectInstance> var7 = list.iterator();
            while (var7.hasNext()) {
                EffectInstance effect = (EffectInstance) var7.next();
                entity.addEffect(new EffectInstance(effect));
            }
        }

    }
}
