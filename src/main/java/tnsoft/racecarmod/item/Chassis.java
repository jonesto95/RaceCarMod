package tnsoft.racecarmod.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tnsoft.racecarmod.RaceCarMod;
import tnsoft.racecarmod.entity.EntityRaceCar2;
import tnsoft.racecarmod.init.ModItems;

public class Chassis extends Item implements IHasModel {

	
	public Chassis()
	{
		setUnlocalizedName("chassis");
		setRegistryName("race:chassis");
		setCreativeTab(ModItems.racingTab);
		ModItems.ITEMS.add(this);
	}
	
	
	@Override
	public void registerModels()
	{
		RaceCarMod.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
