package tnsoft.racecarmod.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import tnsoft.racecarmod.RaceCarMod;
import tnsoft.racecarmod.entity.*;
import tnsoft.racecarmod.entity.render.RenderRaceCar2;
import tnsoft.racecarmod.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ModEntities {	
	
	public static void registerEntities()
	{
		registerEntity("racecar", EntityRaceCar2.class, 120, 50);
	}
	
	
	private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range)
	{
		ResourceLocation rl = new ResourceLocation(Reference.MODID + ":" + name);
		EntityRegistry.registerModEntity(rl, entity, name, id, RaceCarMod.instance, range, 1, true, 555, 555);
	}
	
	
	private static <E extends Entity> EntityEntryBuilder<E> createBuilder(final String name, int entityId)
	{
		final EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
		final ResourceLocation registryName = new ResourceLocation(Reference.MODID, name);
		return builder.id(registryName, entityId).name(registryName.toString());
	}
}
