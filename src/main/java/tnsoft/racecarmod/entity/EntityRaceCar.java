package tnsoft.racecarmod.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import tnsoft.racecarmod.block.CarBoostBlock;
import tnsoft.racecarmod.block.CarFinishBlock;
import tnsoft.racecarmod.block.CarJumpBlock;
import tnsoft.racecarmod.block.CarSlowBlock;

public class EntityRaceCar extends EntityBoat {

	private float momentum;
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
	private double waterLevel;
	private float raceCarGlide;
	private EntityRaceCar.Status previousStatus;
	private EntityRaceCar.Status status;
	private double lastYd;
	private double timeSinceLastFinish;
	
	public static enum Status
	{
		IN_WATER,
		UNDER_WATER,
		UNDER_FLOWING_WATER,
		ON_LAND,
		IN_AIR;
	}
	
	public EntityRaceCar(World worldIn) {
		super(worldIn);
		timeSinceLastFinish = -1;
		// TODO Auto-generated constructor stub
	}

	public EntityRaceCar(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }
	
	
	@Override public void onUpdate()
	{
		previousStatus = status;
		status = getRaceCarStatus();
		
		if(getTimeSinceHit() > 0)
			setTimeSinceHit(getTimeSinceHit());
		
		if(getDamageTaken() > 0)
			setDamageTaken(getDamageTaken());
	
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		super.onUpdate();
		tickLerp();
		
		if(canPassengerSteer())
		{
			setPaddleState(false, false);		
			updateMotion();
			if(world.isRemote)
			{
				controlRaceCart();
				world.sendPacketToServer(new CPacketSteerBoat(false, false));
			}
			move(MoverType.SELF, motionX, motionY, motionZ);
		}
		else
		{
			motionX = 0; motionY = 0; motionZ = 0;
		}
		
		doBlockCollisions();
		List<Entity> list = world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.getTeamCollisionPredicate(this));
		
		if(!list.isEmpty())
		{
			boolean flag = !world.isRemote && !(getControllingPassenger() instanceof EntityPlayer);
			
			for (int j = 0; j < list.size(); ++j)
            {
                Entity entity = list.get(j);

                if (!entity.isPassenger(this))
                {
                    if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer))
                    {
                        entity.startRiding(this);
                    }
                    else
                    {
                        this.applyEntityCollision(entity);
                    }
                }
            }
		}
	}/**/
	
	
	private EntityRaceCar.Status getRaceCarStatus()
	{
		EntityRaceCar.Status resultStatus = getUnderwaterStatus();
		if(resultStatus != null)
		{
			waterLevel = this.getEntityBoundingBox().maxY;
			return resultStatus;
		}
		else if (checkInWater())
		{
			return EntityRaceCar.Status.IN_WATER;
		}
		else
		{
			float f = getRaceCarGlide();
			
			if(f > 0)
			{
				raceCarGlide = f;
				return EntityRaceCar.Status.ON_LAND;
			}
			else
				return EntityRaceCar.Status.IN_AIR;
		}
	}
	
	
	@Nullable
    private EntityRaceCar.Status getUnderwaterStatus()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER && d0 < (double)BlockLiquid.getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos))
                        {
                            if (((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() != 0)
                            {
                                EntityRaceCar.Status resultStatus = EntityRaceCar.Status.UNDER_FLOWING_WATER;
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
            blockpos$pooledmutableblockpos.release();
        }

        return flag ? EntityRaceCar.Status.UNDER_WATER : null;
    }
	
	
	private boolean checkInWater()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER)
                        {
                            float f = BlockLiquid.getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos);
                            this.waterLevel = Math.max((double)f, this.waterLevel);
                            flag |= axisalignedbb.minY < (double)f;
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return flag;
    }
	
	
	public float getRaceCarGlide()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.ceil(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.ceil(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1;
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        float f = 0.0F;
        int k1 = 0;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

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
                                blockpos$pooledmutableblockpos.setPos(l1, k2, i2);
                                IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);
                                iblockstate.addCollisionBoxToList(this.world, blockpos$pooledmutableblockpos, axisalignedbb1, list, this, false);

                                if (!list.isEmpty())
                                {
                                    f += iblockstate.getBlock().getSlipperiness(iblockstate, this.world, blockpos$pooledmutableblockpos, this);
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
            blockpos$pooledmutableblockpos.release();
        }

        return f / (float)k1;
    }
	
	
	private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }
	
	
	private void controlRaceCart()
    {
        if (this.isBeingRidden())
        {
            float f = 0.0F;

            if (this.leftInputDown)
            {
                this.deltaRotation += -1.0F;
            }

            if (this.rightInputDown)
            {
                ++this.deltaRotation;
            }

            if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown)
            {
                f += 0.005F;
            }

            this.rotationYaw += this.deltaRotation;

            if (this.forwardInputDown)
            {
                f += 0.04F;
            }

            if (this.backInputDown)
            {
                f -= 0.005F;
            }

            this.motionX += (double)(MathHelper.sin(-this.rotationYaw * 0.017453292F) * f);
            this.motionZ += (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * f);
            this.setPaddleState(this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown);
        }
    }
	
	
	private void updateMotion()
    {
        double d0 = -0.03999999910593033D;
        double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;
        this.momentum = 0.05F;

        if (previousStatus == EntityRaceCar.Status.IN_AIR && status != EntityRaceCar.Status.IN_AIR && status != EntityRaceCar.Status.ON_LAND)
        {
            waterLevel = this.getEntityBoundingBox().minY + (double)this.height;
            setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.height) + 0.101D, this.posZ);
            motionY = 0.0D;
            this.lastYd = 0.0D;
            status = EntityRaceCar.Status.IN_WATER;
        }
        else
        {
        	d2 = 0.65D;
            this.momentum = 0.9F;
            /*if (this.status == EntityRaceCar.Status.IN_WATER)
            {
                d2 = 0.65D;
                this.momentum = 0.9F;
            }
            else if (this.status == EntityRaceCar.Status.UNDER_FLOWING_WATER)
            {
    			d1 = 0.01D;
    			momentum = 0.9F;
            }
            else if (this.status == EntityRaceCar.Status.UNDER_WATER)
            {
    			d1 = 0.01D;
    			momentum = 0.7F;
            }
            else if (this.status == EntityRaceCar.Status.IN_AIR)
            {
    			if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) d1 = -0.01D;
    			momentum = 0.9F;
            }
            else if (status == EntityRaceCar.Status.ON_LAND)
            {
            	momentum = raceCarGlide;
    			if(getControllingPassenger() instanceof EntityPlayer) raceCarGlide /= 2.0F;
            }*/
            Block firstBlockDown = world.getBlockState((new BlockPos(this).down())).getBlock();
            Block secondBlockDown = world.getBlockState((new BlockPos(this).down().down())).getBlock();
            
            
            if(firstBlockDown instanceof CarJumpBlock) d1 = 0.2D;
            if(firstBlockDown instanceof CarBoostBlock)
            {
    			motionX += (double)(MathHelper.sin(-rotationYaw * 0.017453292F) * 1.2F);
        		motionZ += (double)(MathHelper.cos(rotationYaw * 0.017453292F) * 1.2F);
            }
            if(firstBlockDown instanceof CarSlowBlock) momentum = 0.002F;
            if(firstBlockDown instanceof CarFinishBlock)
            {
            	if(timeSinceLastFinish <= 0)
            	{
            		timeSinceLastFinish = 10;
            		// Send message to server
            	}
            }
            else
            {
            	if(secondBlockDown instanceof CarJumpBlock) d1 = 0.2D;
                if(secondBlockDown instanceof CarBoostBlock)
                {
        			motionX += (double)(MathHelper.sin(-rotationYaw * 0.017453292F) * 1.2F);
            		motionZ += (double)(MathHelper.cos(rotationYaw * 0.017453292F) * 1.2F);
                }
                if(secondBlockDown instanceof CarSlowBlock) momentum = 0.002F;
                if(secondBlockDown instanceof CarFinishBlock)
                {
                	if(timeSinceLastFinish <= 0)
                	{
                		timeSinceLastFinish = 10;
                		// Send message to server
                	}
                }
            }
            if(timeSinceLastFinish > 0)
            	timeSinceLastFinish -= 0.05F;
            this.motionX *= (double)this.momentum;
            this.motionZ *= (double)this.momentum;
            this.deltaRotation *= this.momentum;
            this.motionY += d1;

            if (d2 > 0.0D)
            {
                this.motionY += d2 * 0.06153846016296973D;
                this.motionY *= 0.75D;
            }
        }
    }
}
