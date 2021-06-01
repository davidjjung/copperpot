package com.davigj.copperpot.core.setup;

import com.davigj.copperpot.client.gui.CopperPotScreen;
import com.davigj.copperpot.core.registry.CopperPotBlocks;
import com.davigj.copperpot.core.registry.CopperPotContainerTypes;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.registry.ModBlocks;

@Mod.EventBusSubscriber(
        modid = "copperpot",
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientEventHandler {
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_BOWL = new ResourceLocation("copperpot", "item/empty_container_slot_bowl");
    public static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onStitchEvent(TextureStitchEvent.Pre event) {
        ResourceLocation stitching = event.getMap().getTextureLocation();
        if (stitching.equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            event.addSprite(EMPTY_CONTAINER_SLOT_BOWL);
        }
    }

    public static void init (FMLClientSetupEvent event) {
        ScreenManager.registerFactory((ContainerType) CopperPotContainerTypes.COPPER_POT.get(), CopperPotScreen::new);
        RenderTypeLookup.setRenderLayer((Block) CopperPotBlocks.COPPER_POT.get(), RenderType.getCutout());
    }
}
