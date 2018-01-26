package tnsoft.racecarmod.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import tnsoft.racecarmod.util.handlers.RegistryHandler;

public class ClientProxy extends CommonProxy {
	
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelResourceLocation mrl = new ModelResourceLocation(item.getRegistryName(), id);
		ModelLoader.setCustomModelResourceLocation(item, meta, mrl);
	}
	
	
	@Override public void preInitRegistries()
	{
		RegistryHandler.preInitRegistries();
	}
}
