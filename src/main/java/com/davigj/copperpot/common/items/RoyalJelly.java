package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.registry.CopperPotItems;
import com.google.common.collect.Lists;
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
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.utils.MathUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class RoyalJelly extends Item {
    private static final List<EffectInstance> EFFECTS;

    public RoyalJelly(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            fliparoo(entityLiving);
        }
        return stack;
    }

    public void fliparoo(LivingEntity player) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            if (effect.getDuration() > 10 && effect.getEffectName().equals("effect.minecraft.poison")) {
                if (Math.random() < 0.6) {
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, effect.getDuration() / 2,
                            0, effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
                }
            }
        }
        player.removePotionEffect(Effects.POISON);
    }

    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (target instanceof BeeEntity) {
            BeeEntity bee = (BeeEntity) target;
            if (bee.isAlive() || bee.hasStung()) {
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    static {
        EFFECTS = Lists.newArrayList(new EffectInstance[]{new EffectInstance(Effects.ABSORPTION, 6000, 0), new EffectInstance(Effects.STRENGTH, 6000, 1), new EffectInstance(Effects.RESISTANCE, 6000, 2)});
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
                    if (entity.isChild()) {
                        entity.ageUp(120, true);
                    }
                    Iterator var6 = RoyalJelly.EFFECTS.iterator();

                    while (var6.hasNext()) {
                        EffectInstance effect = (EffectInstance) var6.next();
                        entity.addPotionEffect(new EffectInstance(effect));
                    }
                    if (ModList.get().isLoaded("buzzier_bees")) {
                        entity.addPotionEffect(new EffectInstance(new EffectInstance(getCompatEffect("buzzier_bees", new ResourceLocation(
                                "buzzier_bees", "sunny")).get(), 28800, 0)));
                    }
                    entity.setAngerTime(0);

                    entity.world.playSound((PlayerEntity) null, target.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, 1.2F);

                    for (int i = 0; i < 5; ++i) {
                        double d0 = MathUtils.RAND.nextGaussian() * 0.02D;
                        double d1 = MathUtils.RAND.nextGaussian() * 0.02D;
                        double d2 = MathUtils.RAND.nextGaussian() * 0.02D;
                        entity.world.addParticle((IParticleData) ParticleTypes.FALLING_HONEY, entity.getPosXRandom(1.0D), entity.getPosYRandom() + 0.5D, entity.getPosZRandom(1.0D), d0, d1, d2);
                    }

                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }

                    event.setCancellationResult(ActionResultType.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
}
