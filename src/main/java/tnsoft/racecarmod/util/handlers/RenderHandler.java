package tnsoft.racecarmod.util.handlers;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import tnsoft.racecarmod.entity.EntityRaceCar2;
import tnsoft.racecarmod.entity.render.RenderRaceCar2;
import tnsoft.racecarmod.util.Reference;

public class RenderHandler {

	public static void registerEntityRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityRaceCar2.class, new IRenderFactory<EntityRaceCar2>()
		{
			@Override public Render<? super EntityRaceCar2> createRenderFor(RenderManager manager)
			{
				return new RenderRaceCar2(manager);
			}
		});
	}
}
