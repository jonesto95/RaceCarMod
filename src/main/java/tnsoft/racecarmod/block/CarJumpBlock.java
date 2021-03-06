package tnsoft.racecarmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import tnsoft.racecarmod.RaceCarMod;
import tnsoft.racecarmod.init.ModBlocks;
import tnsoft.racecarmod.init.ModItems;
import tnsoft.racecarmod.item.IHasModel;
import tnsoft.racecarmod.util.Reference;

public class CarJumpBlock extends Block implements IHasModel {
	
	public CarJumpBlock()
	{
		super(Material.WOOD);
		setUnlocalizedName("carjumpblock");
		setRegistryName(Reference.MODID + ":carjumpblock");
		setCreativeTab(ModItems.racingTab);
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}

	@Override
	public void registerModels() {
		RaceCarMod.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
		
	}

}
