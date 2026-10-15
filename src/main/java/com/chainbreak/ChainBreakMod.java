package com.chainbreak;

import com.chainbreak.net.Network;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ChainBreakMod.MODID)
public class ChainBreakMod {
    public static final String MODID = "chainbreak";

    public ChainBreakMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChainConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(new ServerEvents());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Network::register);
    }
}
