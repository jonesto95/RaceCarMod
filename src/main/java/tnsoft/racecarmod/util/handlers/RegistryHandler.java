package tnsoft.racecarmod.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import tnsoft.racecarmod.init.ModBlocks;
import tnsoft.racecarmod.init.ModEntities;
import tnsoft.racecarmod.init.ModItems;
import tnsoft.racecarmod.item.IHasModel;

@EventBusSubscriber
public class RegistryHandler {

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> e)
	{
		e.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
	}

	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> e)
	{
		e.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
	}
	
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent e)
	{
		for(Item item : ModItems.ITEMS)
		{
			if(item instanceof IHasModel)
			{
				((IHasModel)item).registerModels();
			}
		}
		
		for(Block block : ModBlocks.BLOCKS)
		{
			if(block instanceof IHasModel)
			{
				((IHasModel)block).registerModels();
			}
		}
	}
	
	public static void preInitRegistries()
	{
		ModEntities.registerEntities();
		RenderHandler.registerEntityRenders();
	}
	
	
	public static void postInitRegistries()
	{
		
	}
}
