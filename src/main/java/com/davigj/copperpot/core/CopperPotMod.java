package com.davigj.copperpot.core;

import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.core.registry.CopperPotContainerTypes;
import com.davigj.copperpot.core.registry.CopperPotRecipeSerializers;
import com.davigj.copperpot.core.registry.CopperPotTileEntityTypes;
import com.davigj.copperpot.core.setup.ClientEventHandler;
import com.davigj.copperpot.core.setup.CommonEventHandler;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CopperPotMod.MOD_ID)
public class CopperPotMod {
	public static final String MOD_ID = "copperpot";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

	public CopperPotMod() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(ClientEventHandler::init);
		bus.addListener(CommonEventHandler::init);
		bus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);

		REGISTRY_HELPER.register(bus);
		CopperPotTileEntityTypes.TILES.register(bus);
		CopperPotContainerTypes.CONTAINER_TYPES.register(bus);
		CopperPotRecipeSerializers.RECIPE_SERIALIZERS.register(bus);

		MinecraftForge.EVENT_BUS.register(this);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CopperPotConfig.COMMON_SPEC);

		bus.addListener(this::commonSetup);
		bus.addListener(this::clientSetup);
		bus.addListener(this::dataSetup);
	}

	private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
//		CraftingHelper.register(new ResourceLocation(MOD_ID, "tool"), ToolIngredient.SERIALIZER);
		event.getRegistry().register(CopperPotRecipe.SERIALIZER);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}
	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}
	private void dataSetup(GatherDataEvent event) {
	}
}