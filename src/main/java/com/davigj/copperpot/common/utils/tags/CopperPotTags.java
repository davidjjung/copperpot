package com.davigj.copperpot.common.utils.tags;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

public class CopperPotTags {
    public static final ITag.INamedTag<Block> FUME_INHIBITORS = modBlockTag("fume_inhibitors");
    public static final ITag.INamedTag<Block> PARTIAL_FUME_INHIBITORS = modBlockTag("partial_fume_inhibitors");

    public CopperPotTags() {
    }

    private static ITag.INamedTag<Block> modBlockTag(String path) {
        return BlockTags.bind(CopperPotMod.MOD_ID + ":" + path);
    }
}
