//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.davigj.copperpot.common.block.entity;

import com.davigj.copperpot.CopperPotConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vectorwing.farmersdelight.common.tag.ModTags;

public interface SemiHeatableBlockEntity {
    default boolean isHeated(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (CopperPotConfig.COMMON.heatless.get()) {
            return true;
        }
        if (stateBelow.is(ModTags.HEAT_SOURCES)) {
            return stateBelow.hasProperty(BlockStateProperties.LIT) ? (Boolean)stateBelow.getValue(BlockStateProperties.LIT) : true;
        } else {
            if (!this.requiresDirectHeat() && stateBelow.is(ModTags.HEAT_CONDUCTORS)) {
                BlockState stateFurtherBelow = level.getBlockState(pos.below(2));
                if (stateFurtherBelow.is(ModTags.HEAT_SOURCES)) {
                    if (stateFurtherBelow.hasProperty(BlockStateProperties.LIT)) {
                        return (Boolean)stateFurtherBelow.getValue(BlockStateProperties.LIT);
                    }

                    return true;
                }
            }

            return false;
        }
    }

    default boolean requiresDirectHeat() {
        return false;
    }
}
