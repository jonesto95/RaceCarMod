package tnsoft.racecarmod.entity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tnsoft.racecarmod.block.CarBoostBlock;
import tnsoft.racecarmod.block.CarFinishBlock;
import tnsoft.racecarmod.block.CarJumpBlock;
import tnsoft.racecarmod.block.CarSlowBlock;
import tnsoft.racecarmod.init.ModBlocks;
import tnsoft.racecarmod.init.ModItems;

public class EntityRaceCar2 extends Entity {

	private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.<Integer>createKey(EntityRaceCar2.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.<Integer>createKey(EntityRaceCar2.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.<Float>createKey(EntityRaceCar2.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> DRIVER = EntityDataManager.<Integer>createKey(EntityRaceCar2.class, DataSerializers.VARINT);
	
	private float momentum;
	private float outOfControlTicks;
	private float deltaRotation;
	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYaw;
	private double lerpPitch;
	private boolean leftInputDown;
	private boolean rightInputDown;
	private boolean forwardInputDown;
	private boolean backInputDown;
	private boolean jumpInputDown;
	private double waterLevel;
	
	private float raceCarGlide;
	private EntityRaceCar2.Status status;
	private EntityRaceCar2.Status previousStatus;
	private double lastYd;
	
	private int maxPassengers = 2;
	private float fuelRemaining; 
	private int timeSinceLine;
	private boolean hasCrossedLine;
	private int lapTimer;
	
	public static enum Status
	{
		IN_WATER,
		UNDER_WATER,
		UNDER_FLOWING_WATER,
		ON_LAND,
		IN_AIR;
	}
	
	
	public static enum Driver
	{
		DEFAULT(-1, "default"),
		BLACK_DEFAULT(0, "black"),
		RED_DEFAULT(1, "red"),
		GREEN_DEFAULT(2, "green"),
		BROWN_DEFAULT(3, "brown"),
		BLUE_DEFAULT(4, "blue"),
		PURPLE_DEFAULT(5, "purple"),
		CYAN_DEFAULT(6, "cyan"),
		LIGHT_GRAY_DEFAULT(7, "light_gray"),
		GRAY_DEFAULT(8, "gray"),
		PINK_DEFAULT(9, "pink"),
		LIME_DEFAULT(10, "lime"),
		YELLOW_DEFAULT(11, "yellow"),
		LIGHT_BLUE_DEFAULT(12, "light_blue"),
		MAGENTA_DEFAULT(13, "magenta"),
		ORANGE_DEFAULT(14, "orange"),
		WHITE_DEFAULT(15, "white"),
		JONESTO95(16, "jonesto95"),				// Joe
		SERIALSKANKER24(17, "serialskanker24"),	// John
		ADDICTALLICA85(18, "addictallica85"),	// Matt
		DEATH7720(19, "death7720"),				// Jeff
		NONAMEMINI(20, "nonamemini"),			// ZD
		T_B_BADABOOM(21, "t_b_badaboom"),		// Tyler
		ROVENAMI(22, "rovenami"),				// Treck
		SCIENCEPRIMO(23, "scienceprimo"),		// Russel
		MATT2092(24, "matt2092"),				// Duell
		// Andy
		MITCHSHAW95(26, "mitchshaw95");			// Mitch
		
		public final String NAME;
		private final int METADATA;
		
		private Driver(int meta, String name)
		{
			METADATA = meta;
			NAME = name;
		}
		
		public String getName()
		{
			return NAME;
		}
		
		public int getMetaData()
		{
			return METADATA;
		}
		
		public static EntityRaceCar2.Driver byId(int id)
        {
            if (id < 0 || id >= values().length)
                id = 0;

            return values()[id];
        }

        public static EntityRaceCar2.Driver getTypeFromString(String nameIn)
        {
            for (int i = 0; i < values().length; ++i)
            {
                if (values()[i].getName().equals(nameIn))
                    return values()[i];
            }

            return values()[0];
        }
	}
	
	
	public EntityRaceCar2(World worldIn)
	{
		super(worldIn);
		preventEntitySpawning = true;
		setSize(1.375F, 0.5625F);
		fuelRemaining = 0;
		timeSinceLine = 301;
		isImmuneToFire = true;
		hasCrossedLine = false;
	}
	
	
	public EntityRaceCar2(World worldIn, double x, double y, double z)
	{
		this(worldIn);
		setPosition(x, y, z);
		motionX = motionY = motionZ = 0.0D;
		prevPosX = x; prevPosY = y; prevPosZ = z;
	}
	
	
	protected boolean canTriggerWalking()
	{
		return false;
	}
	
	
	protected void entityInit()
	{
		dataManager.register(TIME_SINCE_HIT, Integer.valueOf(0));
		dataManager.register(FORWARD_DIRECTION,  Integer.valueOf(0));
		dataManager.register(DAMAGE_TAKEN, Float.valueOf(0.0F));
		dataManager.register(DRIVER, Integer.valueOf(0));
	}
	
	
	@Nullable public AxisAlignedBB getCollisionBox(Entity entityIn)
	{
		return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
	}
	
	
	@Nullable public AxisAlignedBB getCollisionBoundingBox()
	{
		return getEntityBoundingBox();
	}
	
	
	public boolean canBePushed()
	{
		return true;
	}
	
	
	public double getMountedYOffset()
	{
		return -0.1D;
	}
	
	
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(isEntityInvulnerable(source)) return false;
		
		else if (!world.isRemote && !isDead)
		{
			if(source instanceof EntityDamageSourceIndirect &&
					source.getTrueSource() != null &&
					isPassenger(source.getTrueSource()))
				return false;
			else
			{
				setForwardDirection(-getForwardDirection());
				setTimeSinceHit(10);
				setDamageTaken(getDamageTaken() + amount * 10.0F);
				markVelocityChanged();
				boolean flag = source.getTrueSource() instanceof EntityPlayer &&
						((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;
				if(flag || getDamageTaken() > 40.0F)
				{
					if(!flag && world.getGameRules().getBoolean("doEntityDrops"))
						dropItemWithOffset(getItemRaceCar(), 1, 0.0F);
					setDead();
				}
				return true;
			}
		}
		return false;
	}
	
	
	public void applyEntityCollision(Entity entityIn)
	{
		if(entityIn instanceof EntityRaceCar2)
		{
			if(entityIn.getEntityBoundingBox().minY < getEntityBoundingBox().maxY)
				super.applyEntityCollision(entityIn);
		}
		else if (entityIn.getEntityBoundingBox().minY <= getEntityBoundingBox().minY)
			super.applyEntityCollision(entityIn);
	}
	
	
	public Item getItemRaceCar()
	{
		switch(getDriver())
		{
		case DEFAULT:
			return ModItems.BLANK_CAR;
		case BLACK_DEFAULT:
			return ModItems.BLACK_CAR;
		case ADDICTALLICA85:
			return ModItems.ADDICTALLICA85_CAR;
		case BLUE_DEFAULT:
			return ModItems.BLUE_CAR;
		case BROWN_DEFAULT:
			return ModItems.BROWN_CAR;
		case CYAN_DEFAULT:
			return ModItems.CYAN_CAR;
		case DEATH7720:
			return ModItems.DEATH7720_CAR;
		case GRAY_DEFAULT:
			return ModItems.GRAY_CAR;
		case GREEN_DEFAULT:
			return ModItems.GREEN_CAR;
		case JONESTO95:
			return ModItems.JONESTO95_CAR;
		case LIGHT_BLUE_DEFAULT:
			return ModItems.LIGHT_BLUE_CAR;
		case LIGHT_GRAY_DEFAULT:
			return ModItems.LIGHT_GRAY_CAR;
		case LIME_DEFAULT:
			return ModItems.LIME_CAR;
		case MAGENTA_DEFAULT:
			return ModItems.MAGENTA_CAR;
		case NONAMEMINI:
			return ModItems.NONAMEMINI_CAR;
		case ORANGE_DEFAULT:
			return ModItems.ORANGE_CAR;
		case PINK_DEFAULT:
			return ModItems.PINK_CAR;
		case PURPLE_DEFAULT:
			return ModItems.PURPLE_CAR;
		case RED_DEFAULT:
			return ModItems.RED_CAR;
		case ROVENAMI:
			return ModItems.ROVENAMI_CAR;
		case SCIENCEPRIMO:
			return ModItems.SCIENCEPRIMO_CAR;
		case SERIALSKANKER24:
			return ModItems.SERIALSKANKER24_CAR;
		case T_B_BADABOOM:
			return ModItems.T_B_BADABOOM_CAR;
		case WHITE_DEFAULT:
			return ModItems.WHITE_CAR;
		case YELLOW_DEFAULT:
			return ModItems.YELLOW_CAR;
		case MATT2092:
			return ModItems.MATT2092_CAR;
		case MITCHSHAW95:
			return ModItems.MITCHSHAW95_CAR;
		default:
			break;
		}
		return ModItems.WHITE_CAR;
	}
	
	
	@SideOnly(Side.CLIENT) public void performHurtAnimation()
	{
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11.0F);
	}
	
	
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}
	
	
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch)
	{
		lerpX = x;
		lerpY = y;
		lerpZ = z;
		lerpYaw = (double)yaw;
		lerpPitch = (double)pitch;
		lerpSteps = 10;
	}
	
	
	public EnumFacing getAdjustedHorizontalFacing()
	{
		return getHorizontalFacing().rotateY();
	}
	
	
	public void onUpdate()
	{
		previousStatus = status;
		status = getRaceCarStatus();
		
		if(status != EntityRaceCar2.Status.UNDER_WATER &&
				status != EntityRaceCar2.Status.UNDER_FLOWING_WATER)
			outOfControlTicks = 0.0F;
		else ++outOfControlTicks;
		
		if(!world.isRemote && outOfControlTicks >= 60.0F) removePassengers();
		
		if(getTimeSinceHit() > 0)
			setTimeSinceHit(getTimeSinceHit() - 1);
		
		if(getDamageTaken() > 0.0F)
			setDamageTaken(getDamageTaken() - 1.0F);
		
		if(getControllingPassenger() instanceof EntityPlayer)
			((EntityPlayer)getControllingPassenger()).addPotionEffect(new PotionEffect(Potion.getPotionById(12), 2));
		
		prevPosX = posX; prevPosY = posY; prevPosZ = posZ;
		super.onUpdate();
		tickLerp();
				
		if(timeSinceLine <= 500) 
			timeSinceLine++;
		if(hasCrossedLine)
			lapTimer++;
		
		if(canPassengerSteer())
		{
			updateMotion();
			
			if(world.isRemote) controlRaceCar();
			move(MoverType.SELF, motionX, motionY, motionZ);
			
			double linearVelocity = MathHelper.sqrt(motionX * motionX + motionZ * motionZ); // Is never greater than 1 for some reason
			if(forwardInputDown || backInputDown)
			{
				System.out.println(fuelRemaining);
				fuelRemaining -= 2 * Math.min(linearVelocity, 0.8);
			}
			if(fuelRemaining <= 0 && !getPassengers().isEmpty())
			{
				if(getControllingPassenger() instanceof EntityPlayer)
				{
					ItemStack is = findCoal((EntityPlayer)getControllingPassenger());
					if(is == null)
					{
						((EntityPlayer)getControllingPassenger()).sendStatusMessage(new TextComponentString("You ran out of coal."), true);
						removePassengers();
					}
				}
			}
		}
		else
		{
			motionX = 0.0D;
			motionY = 0.0D;
			motionZ = 0.0D;
		}
		
		doBlockCollisions();
		List<Entity> list = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.getTeamCollisionPredicate(this));
		
		if(!list.isEmpty())
		{
			boolean flag = !world.isRemote && !(getControllingPassenger() instanceof EntityPlayer);
			
			for(int j = 0; j < list.size(); ++j)
			{
				Entity entity = list.get(j);
				
				if(!entity.isPassenger(this))
				{
					if(flag && getPassengers().size() < maxPassengers && !entity.isRiding() && entity.width < width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer))
						entity.startRiding(this);
					else
						applyEntityCollision(entity);
				}
			}
		}
		else
		{
			hasCrossedLine = false;
		}
	}
	
	
	private void tickLerp()
	{
		if(lerpSteps > 0 && !canPassengerSteer())
		{
			double d0 = posX + (lerpX - posX) / (double)lerpSteps;
			double d1 = posY + (lerpY - posY) / (double)lerpSteps;
			double d2 = posZ + (lerpZ - posZ) / (double)lerpSteps;
			double d3 = MathHelper.wrapDegrees(lerpYaw - (double)rotationYaw);
			rotationYaw = (float)((double)rotationYaw + d3 / (double)lerpSteps);
			rotationPitch = (float)((double)rotationPitch + (lerpPitch - rotationPitch) / (double)lerpSteps);
			lerpSteps--;
			setPosition(d0, d1, d2);
			setRotation(rotationYaw, rotationPitch);
		}
	}
	
	
	private EntityRaceCar2.Status getRaceCarStatus()
	{
		EntityRaceCar2.Status resultStatus = getUnderwaterStatus();
		
		if(resultStatus != null)
		{
			waterLevel = getEntityBoundingBox().maxY;
			return resultStatus;
		}
		else if (checkInWater())
			return EntityRaceCar2.Status.IN_WATER;
		else
		{
			float f = getRaceCarGlide();
			
			if(f > 0.0F)
			{
				raceCarGlide = f;
				return EntityRaceCar2.Status.ON_LAND;
			}
			else return EntityRaceCar2.Status.IN_AIR;
		}
	}
	
	
	public float getWaterLevelAbove()
	{
		AxisAlignedBB axisAlignedBB = getEntityBoundingBox();
		int i = (int)axisAlignedBB.minX;
		int j = MathHelper.ceil(axisAlignedBB.maxX);
		int k = (int)axisAlignedBB.maxY;
		int l = MathHelper.ceil(axisAlignedBB.maxY - lastYd);
		int i1 = (int)axisAlignedBB.minZ;
		int j1 = MathHelper.ceil(axisAlignedBB.maxZ);
		BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
		
		try
		{
			label108:
				
			for(int k1 = k; k1 < l; ++k1)
			{
				float f = 0.0F;
				int l1 = i;
				
				while(true)
				{
					if(l1 >= j)
					{
						if(f < 1.0F)
						{
							float f2 = (float)pooledMutableBlockPos.getY() + f;
							return f2;
						}
						break;
					}
					
					for(int i2 = i1; i2 < j1; ++i2)
					{
						pooledMutableBlockPos.setPos(l1, k1, i2);
						IBlockState iBlockState = world.getBlockState(pooledMutableBlockPos);
						
						if(iBlockState.getMaterial() == Material.WATER)
							f = Math.max(f, BlockLiquid.getBlockLiquidHeight(iBlockState,  world, pooledMutableBlockPos));
						
						if(f >= 1.0F) continue label108;
					}
					
					++l1;
				}
			}
		
			return (float)(l+1);
		}
		finally
		{
			pooledMutableBlockPos.release();
		}
	}
	
	
	public float getRaceCarGlide()
	{
		AxisAlignedBB axisAlignedBB = getEntityBoundingBox();
		AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(axisAlignedBB.minX, axisAlignedBB.minY - 0.001D, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
		int i = (int)(axisAlignedBB1.minX) - 1;
		int j = MathHelper.ceil(axisAlignedBB1.maxX) + 1;
		int k = (int)(axisAlignedBB1.minY) - 1;
		int l = MathHelper.ceil(axisAlignedBB1.maxY) + 1;
		int i1 = (int)(axisAlignedBB1.minZ) - 1;
		int j1 = MathHelper.ceil(axisAlignedBB1.maxZ) + 1;
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
		
		float f = 0.0f;
		int k1 = 0;
		BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
		
		try
        {
            for (int l1 = i; l1 < j; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                    if (j2 != 2)
                    {
                        for (int k2 = k; k2 < l; ++k2)
                        {
                            if (j2 <= 0 || k2 != k && k2 != l - 1)
                            {
                            	pooledMutableBlockPos.setPos(l1, k2, i2);
                                IBlockState iblockstate = world.getBlockState(pooledMutableBlockPos);
                                iblockstate.addCollisionBoxToList(world, pooledMutableBlockPos, axisAlignedBB1, list, this, false);

                                if (!list.isEmpty())
                                {
                                    f += iblockstate.getBlock().getSlipperiness(iblockstate, world, pooledMutableBlockPos, this);
                                    ++k1;
                                }

                                list.clear();
                            }
                        }
                    }
                }
            }
        }
        finally
        {
        	pooledMutableBlockPos.release();
        }
		
		return f / (float)k1;
	}
	
	
	private boolean checkInWater()
	{
		AxisAlignedBB axisAlignedBB = getEntityBoundingBox();
		int i = (int)axisAlignedBB.minX;
		int j = MathHelper.ceil(axisAlignedBB.maxX);
		int k = (int)axisAlignedBB.minY;
		int l = MathHelper.ceil(axisAlignedBB.minY + 0.001D);
		int i1 = (int)axisAlignedBB.minZ;
		int j1 = MathHelper.ceil(axisAlignedBB.maxZ);
		
		boolean flag = false;
		waterLevel = Double.MIN_VALUE;
		BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
		
		try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                    	pooledMutableBlockPos.setPos(k1, l1, i2);
                        IBlockState iblockstate = world.getBlockState(pooledMutableBlockPos);

                        if (iblockstate.getMaterial().isLiquid())
                        {
                            float f = BlockLiquid.getLiquidHeight(iblockstate, world, pooledMutableBlockPos);
                            waterLevel = Math.max((double)f, waterLevel);
                            flag |= axisAlignedBB.minY < (double)f;
                        }
                    }
                }
            }
        }
        finally
        {
        	pooledMutableBlockPos.release();
        }

        return flag;
	}
	
	
	@Nullable private EntityRaceCar2.Status getUnderwaterStatus()
	{
		AxisAlignedBB axisAlignedBB = getEntityBoundingBox();
		double d0 = axisAlignedBB.maxY + 0.001D;
		int i = (int)axisAlignedBB.minX;
		int j = MathHelper.ceil(axisAlignedBB.maxX);
		int k = (int)axisAlignedBB.maxY;
		int l = MathHelper.ceil(d0);
		int i1 = (int)axisAlignedBB.minZ;
		int j1 = MathHelper.ceil(axisAlignedBB.maxZ);
		boolean flag = false;
		BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
		
		try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                    	pooledMutableBlockPos.setPos(k1, l1, i2);
                        IBlockState iblockstate = world.getBlockState(pooledMutableBlockPos);

                        if (iblockstate.getMaterial() == Material.WATER && d0 < (double)BlockLiquid.getLiquidHeight(iblockstate, world, pooledMutableBlockPos))
                        {
                            if (((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() != 0)
                            {
                                EntityRaceCar2.Status resultStatus = EntityRaceCar2.Status.UNDER_FLOWING_WATER;
                                return resultStatus;
                            }

                            flag = true;
                        }
                    }
                }
            }
        }
        finally
        {
        	pooledMutableBlockPos.release();
        }

        return flag ? EntityRaceCar2.Status.UNDER_WATER : null;
	}
	
	
	private void updateMotion()
	{
		double d0 = -0.04D;
		double d1 = hasNoGravity() ? 0.0D : -0.04D;
		double d2 = 0.0D;
		
		if(previousStatus == EntityRaceCar2.Status.IN_AIR && status != previousStatus && status != EntityRaceCar2.Status.ON_LAND)
		{
			waterLevel = getEntityBoundingBox().minY + (double)height;
			setPosition(posX, (double)(getWaterLevelAbove() - height) + 0.101D, posZ);
			motionY = 0.0D;
			lastYd = 0.0D;
			status = EntityRaceCar2.Status.IN_WATER;
		}
		else
		{
			momentum = 0.95F;
			switch(status)
			{
				case IN_WATER:
					d2 = 0.65f;
					break;
					
				case UNDER_FLOWING_WATER:
					d1 = 7.0E-4D;
					break;
					
				case UNDER_WATER:
					d2 = 0.001D;
					momentum = 0.45F;
					break;
					
				case IN_AIR: 
					momentum = 1;
					break;
					
				case ON_LAND:
					d2 = 0.65F;
					break;
			}
			
			motionX *= (double)momentum;
			motionZ *= (double)momentum;
			deltaRotation *= momentum;
			motionY += d1;
			
			if(d2 > 0.0D)
			{
				motionY += d2 * 0.0615D;
				motionY *= 0.75D;
			}
		}
		
		d1 = checkBlocks(d1);
	}
	
	
	private double checkBlocks(double prevD1)
	{
		double d1 = prevD1;
		
		// Check blocks beneath
		Block firstBlock = world.getBlockState((new BlockPos(this).down())).getBlock();
		Block secondBlock = world.getBlockState((new BlockPos(this).down().down())).getBlock();
		Block thirdBlock = world.getBlockState((new BlockPos(this).down().down().down())).getBlock();
		Block fourthBlock = world.getBlockState((new BlockPos(this).down().down().down().down())).getBlock();
		
		if(firstBlock instanceof CarBoostBlock)
		{
			motionX *= 1.3F;
			motionZ *= 1.3F;
		}
		else if(firstBlock instanceof CarJumpBlock)
			motionY = 0.5F;
		else if(firstBlock instanceof CarSlowBlock)
		{
			motionX *= 0.1F;
    		motionZ *= 0.1F;
		}
		else if(firstBlock instanceof CarFinishBlock)
			crossFinishLine();
		else
		{
			if(secondBlock instanceof CarBoostBlock)
			{
				motionX *= 1.3F;
				motionZ *= 1.3F;
			}
			else if(secondBlock instanceof CarJumpBlock)
				motionY = 0.5F;
			else if(secondBlock instanceof CarSlowBlock)
			{
				motionX *= 0.1F;
	    		motionZ *= 0.1F;
			}
			else if(secondBlock instanceof CarFinishBlock)
				crossFinishLine();
			else
			{
				if(thirdBlock instanceof CarFinishBlock)
					crossFinishLine();
				else if(fourthBlock instanceof CarFinishBlock)
					crossFinishLine();
			}
		}
		return d1;
	}
	
	
	private void crossFinishLine()
	{
		if(getControllingPassenger() instanceof EntityPlayer && timeSinceLine >= 50)
		{
			EntityPlayer passenger = (EntityPlayer)getControllingPassenger();
			String username = passenger.getName();
			if(hasCrossedLine)
				passenger.sendMessage(new TextComponentString(username + " has crossed the line! (Time: " +  lapTimer + ")"));
			else
			{					
				passenger.sendMessage(new TextComponentString(username + " has crossed the line!"));
				hasCrossedLine = true;
			}
			timeSinceLine = 0;
			lapTimer = 0;
		}
	}
	
	private void controlRaceCar()
    {
        if (isBeingRidden())
        {
            float f = 0.0F;
            
            leftInputDown = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft);
        	rightInputDown = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight);
        	forwardInputDown = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward);
        	backInputDown = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack);
    	    jumpInputDown = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump);
    	    
            if (leftInputDown)
                deltaRotation += -1.0F;

            if (rightInputDown)
                ++deltaRotation;

            if (rightInputDown != leftInputDown && !forwardInputDown && !backInputDown)
                f += 0.005F;

            rotationYaw += deltaRotation;

            if(status != EntityRaceCar2.Status.IN_AIR)
            {
            	if (forwardInputDown)
            		f += 0.04F;
            	
            	if (backInputDown)
            		f -= 0.005F;            	
            }
            else
            {
            	forwardInputDown = false;
            	backInputDown = false;
            }

            if(jumpInputDown && status != EntityRaceCar2.Status.IN_AIR)
            {
            	motionY = 0.5F;
        		fuelRemaining -= 1;
            }
            
            motionX += (double)(MathHelper.sin(-rotationYaw * 0.017453292F) * f);
            motionZ += (double)(MathHelper.cos(rotationYaw * 0.017453292F) * f);
        }
    }
	
	
	public void updatePassenger(Entity passenger)
	{
		if(isPassenger(passenger))
		{
			float f= 0.0F;
			float f1 = (float)((isDead ? 0.01D : getMountedYOffset()) + passenger.getYOffset());
			
			if(getPassengers().size() > 1)
			{
				int i = getPassengers().indexOf(passenger);
				
				f = (i == 0 ? 0.2F : -0.6F);
				
				if(passenger instanceof EntityAnimal)
					f += 0.2F;
			}
			
			Vec3d vec3d = new Vec3d((double)f, 0.0D, 0.0D).rotateYaw(-rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
			passenger.setPosition(posX + vec3d.x, posY + f1, posZ + vec3d.z);
			passenger.rotationYaw += deltaRotation;
			passenger.setRotationYawHead(passenger.getRotationYawHead() + deltaRotation);
			applyYawToEntity(passenger);
			
			if(passenger instanceof EntityAnimal && getPassengers().size() > 1)
			{
				float j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
				passenger.setRenderYawOffset(((EntityAnimal)passenger).renderYawOffset + j);
				passenger.setRotationYawHead(passenger.getRotationYawHead() + j);
			}
		}
	}
	
	
	protected void applyYawToEntity(Entity entityToUpdate)
	{
		entityToUpdate.setRenderYawOffset(rotationYaw);
		float f= MathHelper.wrapDegrees(entityToUpdate.rotationYaw - rotationYaw);
		float f1 = MathHelper.clamp(f, -180, 180);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}
	
	
	@SideOnly(Side.CLIENT)
	public void applyOrientationToEntity(Entity entityToUpdate)
	{
		applyYawToEntity(entityToUpdate);
	}
	
	
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setString("Driver", getDriver().getName());
		compound.setString("RemainingFuel", Float.toString(fuelRemaining));
	}
	
	
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		if(compound.hasKey("Driver", 8))
			setDriver(EntityRaceCar2.Driver.getTypeFromString(compound.getString("Driver")));
		if(compound.hasKey("RemainingFuel", 8))
			fuelRemaining = Float.parseFloat(compound.getString("RemainingFuel"));
	}
	
	
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if(player.isSneaking()) return false;
		
		else
		{
			if(!world.isRemote && outOfControlTicks < 60.0F)
				player.startRiding(this);
			return true;
		}
	}
	
	
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
	{
		lastYd = motionY;
		if(isRiding())
		{
			if(onGroundIn)
			{
				if(fallDistance > 3.0F)
				{
					if(status != EntityRaceCar2.Status.ON_LAND)
					{
						fallDistance = 0.0F;
						return;
					}
					
					fall(fallDistance, 1.0F);
					
					if(!world.isRemote && !isDead)
					{
						setDead();
						
						if(world.getGameRules().getBoolean("doEntityDrops"))
							dropItemWithOffset(getItemRaceCar(), 1, 0.0F);							
					}
				}
				
				fallDistance = 0.0F;
			}
		}
		else if(world.getBlockState((new BlockPos(this)).down()).getMaterial() != Material.WATER && y < 0.0D)
				fallDistance = fallDistance - (float)y;
	}
	
	public void setDamageTaken(float damageTaken)
	{
		dataManager.set(DAMAGE_TAKEN, Float.valueOf(damageTaken));
	}
	
	
	public float getDamageTaken()
	{
		return((Float)dataManager.get(DAMAGE_TAKEN)).floatValue();
	}
	
	
	public void setTimeSinceHit(int timeSinceHit)
	{
		dataManager.set(TIME_SINCE_HIT, Integer.valueOf(timeSinceHit));
	}
	
	
	public int getTimeSinceHit()
	{
		return ((Integer)dataManager.get(TIME_SINCE_HIT)).intValue();
	}
	
	
	public void setForwardDirection(int forwardDirection)
	{
		dataManager.set(FORWARD_DIRECTION, Integer.valueOf(forwardDirection));
	}
	
	
	public int getForwardDirection()
	{
		return ((Integer)dataManager.get(FORWARD_DIRECTION)).intValue();
	}
	
	
	public void setDriver(EntityRaceCar2.Driver driver)
	{
		dataManager.set(DRIVER, Integer.valueOf(driver.ordinal()));
	}
	
	
	public EntityRaceCar2.Driver getDriver()
	{
		return EntityRaceCar2.Driver.byId(((Integer)dataManager.get(DRIVER)).intValue());
	}
	
	
	protected boolean canFitPassenger(Entity passenger)
	{
		return getPassengers().size() < maxPassengers;
	}
	
	
	@Nullable public Entity getControllingPassenger()
	{
		List<Entity> list = getPassengers();
		return list.isEmpty() ? null : (Entity)list.get(0);
	}
	
	
	@Override protected void addPassenger(Entity passenger)
	{
		if(passenger instanceof EntityPlayer && fuelRemaining <= 0)
		{
			ItemStack coalStack = findCoal((EntityPlayer)passenger);
			if(coalStack == null) 
			{
				((EntityPlayer)passenger).sendStatusMessage(new TextComponentString("You need coal or charcoal to drive"), true);
				return;
			}
		}
		super.addPassenger(passenger);
		if(canPassengerSteer() && lerpSteps > 0)
		{
			lerpSteps = 0;
			posX = lerpX;
			posY = lerpY;
			posZ = lerpZ;
			rotationYaw = (float)lerpYaw;
			rotationPitch = (float)lerpPitch;
		}
	}
	
	
	protected boolean isCoal(ItemStack stack)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block.equals(Block.getBlockById(173)) || block.getUnlocalizedName().equals("charcoalblock"))
		{
			fuelRemaining = 2000;
			return true;
		}
		else if (stack.getItem() instanceof ItemCoal)
		{
			fuelRemaining = 200;
			return true;
		}
		return false;
	}
    
	
    private ItemStack findCoal(EntityPlayer player)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (isCoal(itemstack)) 
            {
            	itemstack.setCount(itemstack.getCount() - 1);
        		player.inventory.setInventorySlotContents(i, itemstack);
            	return itemstack;
            }
        }
        return null;
    }
}
