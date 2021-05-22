package com.davigj.copperpot.common.blocks;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MeringueBlock extends BreakableBlock {
    protected static final VoxelShape SHAPES = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    public MeringueBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES;
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (this.isRiding(entityIn)) {
            this.Ride(entityIn);
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    private boolean isRiding(Entity entity) {
        Vector3d vector3d = entity.getMotion();
        if (Math.abs(vector3d.y) > 0.1) {
            return true;
        }
        return false;
    }

    private void Ride(Entity entity) {
        Vector3d vector3d = entity.getMotion();
        double amp = 0;
        if (entity instanceof LivingEntity) {
            if (entity instanceof PlayerEntity) {
                EffectInstance[] effects = {((PlayerEntity) entity).getActivePotionEffect(Effects.SPEED),
                        ((PlayerEntity) entity).getActivePotionEffect(Effects.JUMP_BOOST),
                        ((PlayerEntity) entity).getActivePotionEffect(Effects.SLOW_FALLING),
                        ((PlayerEntity) entity).getActivePotionEffect(Effects.SLOWNESS)};
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
            if (((LivingEntity) entity).isJumping && vector3d.y > 0) {
//                // we know you're ascending. you're ascending at a speed bounded by
//                // 0.3 + (amp*0.1) or 0
////                entity.setMotion(vector3d.x, Math.max(0.3 + (amp * 0.1D), vector3d.y+ (amp * 0.1D)), vector3d.z);
//
//                if (((LivingEntity) entity).isJumping && vector3d.y > 0) {
//                    // we know you're ascending. you're ascending at a speed bounded by
//                    // 0.3 + (amp*0.1) or 0. If amp is positive, 0.3 + (amp*0.1) is the top bound.
//                    // If amp is negative, 0 is the bottom bound. You maintain your normal v3y speed
//                    // if your calculation exceeds the top bound, and stop if it looks to exceed the bottom bound.
//                    // In other words, amp * 0.1 cannot go below -0.3.
//                    if (vector3d.y < 0.3 + (amp * 0.1D) && amp > 0) {
//                        entity.setMotion(new Vector3d(vector3d.x, vector3d.y + (amp * 0.1D), vector3d.z));
//                    } else if (vector3d.y < 0.3 + (amp * 0.1D) && amp <= 0) {
//                        entity.setMotion(new Vector3d(vector3d.x, Math.max(0.03, vector3d.y + (amp * 0.1D)), vector3d.z));
//                    }
//                    if (rand > 0.99) {
//                        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
//                    }
//                } else if (vector3d.y < 0D) {
//                    if(amp < 0) {
//                        entity.setMotion(new Vector3d(vector3d.x, Math.min(-0.03, Math.max(-0.1D, -0.1D - (amp * 0.05D))), vector3d.z));
//                    } else {
//                        entity.setMotion(new Vector3d(vector3d.x, -0.1D, vector3d.z));
//                    }
//                    if (rand > 0.99) {
//                        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
//                    }
//                }
                if (Math.abs(vector3d.y) < 0.3 + (amp * 0.1D)) {
                    entity.setMotion(new Vector3d(vector3d.x, Math.max(vector3d.y, vector3d.y + ((amp + 1) * 0.1D)), vector3d.z));
                } else {
                    entity.setMotion(new Vector3d(vector3d.x, vector3d.y, vector3d.z));
                }
                if (Math.random() > 0.99) {
                    entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
                }
            } else if (vector3d.y < 0D) {
                if(amp>=0) {
                    entity.setMotion(new Vector3d(vector3d.x, -0.1D, vector3d.z));
                }
                if (amp < 0) {
                    // Magic number is -0.03 so far
                    entity.setMotion(new Vector3d(vector3d.x, Math.min(0.03D, -0.03D - (amp * 0.1)), vector3d.z));
                }
                if (Math.random() > 0.99) {
                    entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
                }
            }
            entity.fallDistance = 0.0F;
        }
    }

    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        entityIn.playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 0.6F, 1.0F);
        if (entityIn.onLivingFall(fallDistance, 0.3F)) {
            entityIn.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.6F, this.soundType.getPitch() * 0.75F);
        }
    }
}
