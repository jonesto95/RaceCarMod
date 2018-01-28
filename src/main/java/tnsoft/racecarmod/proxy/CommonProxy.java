package tnsoft.racecarmod.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import tnsoft.racecarmod.init.ModEntities;
import tnsoft.racecarmod.util.handlers.RegistryHandler;

public class CommonProxy {

	public void registerItemRenderer(Item item, int meta, String id) { }
	
	public void preInitRegistries(){ 
		ModEntities.registerEntities();
	}
}