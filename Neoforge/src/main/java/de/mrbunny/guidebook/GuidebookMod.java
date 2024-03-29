package de.mrbunny.guidebook;

import com.mojang.logging.LogUtils;
import de.mrbunny.guidebook.api.proxy.IProxy;
import de.mrbunny.guidebook.api.util.References;
import de.mrbunny.guidebook.cfg.ModConfigManager;
import de.mrbunny.guidebook.cfg.ModConfigurations;
import de.mrbunny.guidebook.proxy.ClientProxy;
import de.mrbunny.guidebook.proxy.CommonProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(References.GUIDEBOOKAPI_ID)
public class GuidebookMod {

    private static final Logger LOGGER = LogUtils.getLogger();

    @OnlyIn(Dist.CLIENT)
    public static ClientProxy CLIENT_PROXY;

    public GuidebookMod ( IEventBus pBus ) {
        pBus.addListener(this::commonSetup);
        pBus.addListener(this::loadComplete);
    }

    private void commonSetup (final FMLCommonSetupEvent pEvent) {
        ModConfigManager.setupConfigurations();

        if (ModConfigManager.COMMON == null || ModConfigManager.CLIENT == null)
            throw new NullPointerException("Client config can't be null or Common configurations can't be null in commonSetup phase");

        if (FMLLoader.getDist().isClient()) {
            CLIENT_PROXY = new ClientProxy();
        }
    }

    private void loadComplete (final FMLLoadCompleteEvent pEvent) {
        CLIENT_PROXY.initColors();
    }

}
