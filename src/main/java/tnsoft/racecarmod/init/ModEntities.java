package tnsoft.racecarmod.init;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import tnsoft.racecarmod.RaceCarMod;
import tnsoft.racecarmod.entity.*;
import tnsoft.racecarmod.util.Reference;

public class ModEntities {

	public static void registerEntities()
	{
		registerEntity("racecar", EntityRaceCar.class, Reference.ENTITY_RACE_CAR, 50, 16724555, 4682998);
		registerEntity("racecar2", EntityRaceCar2.class, Reference.ENTITY_RACE_CAR+1, 50, 16724555, 4682998);
	}
	
	
	private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range, int colorPrimary, int colorSecondary)
	{
		ResourceLocation rl = new ResourceLocation(Reference.MODID + ":" + name);
		EntityRegistry.registerModEntity(rl, entity, name, id, RaceCarMod.instance, range, 1, true, colorPrimary, colorSecondary);
	}
	
}
