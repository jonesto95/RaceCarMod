package tnsoft.racecarmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tnsoft.racecarmod.proxy.CommonProxy;
import tnsoft.racecarmod.util.Reference;
import tnsoft.racecarmod.util.handlers.RegistryHandler;

@Mod(modid=Reference.MODID, name=Reference.MOD_NAME, version = Reference.VERSION)
public class RaceCarMod {

	@Instance public static RaceCarMod instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static CommonProxy proxy;
	
	
	@EventHandler public static void preInit(FMLPreInitializationEvent e)
	{
		proxy.preInitRegistries();
		// RegistryHandler.preInitRegistries();
	}
	
	
	@EventHandler public static void init(FMLInitializationEvent e)
	{
		
	}
	
	@EventHandler public static void postInit(FMLPostInitializationEvent e)
	{
		
	}
}
