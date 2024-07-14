package com.davigj.copperpot.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MeringueBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MeringueBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (this.isRiding(pEntity)) {
            this.ride(pEntity);
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    private boolean isRiding(Entity entity) {
        Vec3 vector3d = entity.getDeltaMovement();
        return Math.abs(vector3d.y) > 0.1;
    }

    private void ride(Entity entity) {
        Vec3 vector3d = entity.getDeltaMovement();
        double amp = 0;
        if (entity instanceof LivingEntity living) {
            if (entity instanceof Player player) {
                MobEffectInstance[] effects = {player.getEffect(MobEffects.MOVEMENT_SPEED),
                        player.getEffect(MobEffects.JUMP),
                        player.getEffect(MobEffects.SLOW_FALLING),
                        player.getEffect(MobEffects.MOVEMENT_SLOWDOWN)};
                for (int currentEffect = 0; currentEffect < 4; currentEffect++) {
                    if (effects[currentEffect] != null) {
                        if (currentEffect < 2) {
                            amp = effects[currentEffect].getAmplifier() + 1;
                        } else {
                            amp = amp - (effects[currentEffect].getAmplifier() + 1);
                        }
                    }
                }
            }
            if (living.jumping && vector3d.y > 0) {
                if (Math.abs(vector3d.y) < 0.3 + (amp * 0.1D)) {
                    entity.setDeltaMovement(new Vec3(vector3d.x, Math.max(vector3d.y, vector3d.y + ((amp + 1) * 0.1D)), vector3d.z));
                } else {
                    entity.setDeltaMovement(new Vec3(vector3d.x, vector3d.y, vector3d.z));
                }
                if (Math.random() > 0.99) {
                    entity.playSound(SoundEvents.HONEY_BLOCK_STEP, 0.4F, 1.1F);
                }
            } else if (vector3d.y < 0D) {
                if (amp >= 0) {
                    entity.setDeltaMovement(new Vec3(vector3d.x, -0.1D, vector3d.z));
                }
                if (amp < 0) {
                    // Magic number is -0.03 so far
                    entity.setDeltaMovement(new Vec3(vector3d.x, Math.min(0.03D, -0.03D - (amp * 0.1)), vector3d.z));
                }
                if (Math.random() > 0.99) {
                    entity.playSound(SoundEvents.HONEY_BLOCK_STEP, 0.4F, 1.1F);
                }
            }
            entity.fallDistance = 0.0F;
        }
    }

    @Override
    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        pEntity.playSound(SoundEvents.HONEY_BLOCK_FALL, 0.6F, 1.0F);
        if (pEntity.causeFallDamage(pFallDistance, 0.3F, pEntity.damageSources().fall())) {
            pEntity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.6F, this.soundType.getPitch() * 0.75F);
        }
    }
}
