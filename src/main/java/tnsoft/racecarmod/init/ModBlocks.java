package tnsoft.racecarmod.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import tnsoft.racecarmod.block.*;

public class ModBlocks {

	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	public static final Block CAR_JUMP = new CarJumpBlock();
	public static final Block CAR_BOOST = new CarBoostBlock();
	public static final Block CAR_SLOW = new CarSlowBlock();
	public static final Block CAR_FINISH = new CarFinishBlock();
	public static final Block CHARCOAL_BLOCK = new CharcoalBlock();
}
