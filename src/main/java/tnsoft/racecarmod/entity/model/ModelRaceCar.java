package tnsoft.racecarmod.entity.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.IMultipassModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRaceCar extends ModelBase {
	public ModelRenderer body;
    public ModelRenderer spoiler;
    public ModelRenderer tire2;
    public ModelRenderer tire1;
    public ModelRenderer tire3;
    public ModelRenderer tire4;

    public ModelRaceCar() {
    	this.textureWidth = 256;
        this.textureHeight = 128;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(-0.0F, -1.0F, 0.0F);
        this.body.addBox(-32.0F, 0.0F, -18.0F, 64, 35, 36);
        this.setRotationAngles(this.body, 0.0F, 1.5707963267948966F, 0.0F);
        this.spoiler = new ModelRenderer(this, 0, 0);
        this.spoiler.setRotationPoint(-0.3F, 0.0F, 16.0F);
        this.spoiler.addBox(0.0F, -2.3F, -6.9F, 1, 2, 14);
        this.setRotationAngles(this.spoiler, 0.0F, 1.5707963267948966F, 0.0F);
        this.tire1 = new ModelRenderer(this, 0, 0);
        this.tire1.setRotationPoint(10.0F, 5.0F, 8.0F);
        this.tire1.addBox(-3.0F, -3.0F, 0.0F, 6, 6, 1);
        this.setRotationAngles(this.tire1, 0.0F, 1.5707963267948966F, 0.0F);
        this.tire2 = new ModelRenderer(this, 0, 0);
        this.tire2.setRotationPoint(-10.0F, 5.0F, 8.0F);
        this.tire2.addBox(-3.0F, -3.0F, 0.0F, 6, 6, 1);
        this.setRotationAngles(this.tire2, 0.0F, 1.5707963267948966F, 0.0F);
        this.tire3 = new ModelRenderer(this, 0, 0);
        this.tire3.setRotationPoint(10.0F, 5.0F, -8.0F);
        this.tire3.addBox(-3.0F, -3.0F, 0.0F, 6, 6, 1);
        this.setRotationAngles(this.tire3, 0.0F, 1.5707963267948966F, 0.0F);
        this.tire4 = new ModelRenderer(this, 0, 0);
        this.tire4.setRotationPoint(-10.0F, 5.0F, -8.0F);
        this.tire4.addBox(-3.0F, -3.0F, 0.0F, 6, 6, 1);
        this.setRotationAngles(this.tire4, 0.0F, 1.5707963267948966F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.body.offsetX, this.body.offsetY, this.body.offsetZ);
        GlStateManager.translate(this.body.rotationPointX * scale, this.body.rotationPointY * scale, this.body.rotationPointZ * scale);
        GlStateManager.scale(0.5F, 0.2F, 0.5F);
        GlStateManager.translate(-this.body.offsetX, -this.body.offsetY, -this.body.offsetZ);
        GlStateManager.translate(-this.body.rotationPointX * scale, -this.body.rotationPointY * scale, -this.body.rotationPointZ * scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.body.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.spoiler.offsetX, this.spoiler.offsetY, this.spoiler.offsetZ);
        GlStateManager.translate(this.spoiler.rotationPointX * scale, this.spoiler.rotationPointY * scale, this.spoiler.rotationPointZ * scale);
        GlStateManager.scale(1.2F, 1.3F, 1.1F); // 0.5f, 1.3f, 1.1f
        GlStateManager.translate(-this.spoiler.offsetX, -this.spoiler.offsetY, -this.spoiler.offsetZ);
        GlStateManager.translate(-this.spoiler.rotationPointX * scale, -this.spoiler.rotationPointY * scale, -this.spoiler.rotationPointZ * scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.spoiler.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.tire1.offsetX, this.tire1.offsetY, this.tire1.offsetZ);
        GlStateManager.translate(this.tire1.rotationPointX * scale, this.tire1.rotationPointY * scale, this.tire1.rotationPointZ * scale);
        GlStateManager.scale(0.9F, 0.9F, 1.0F);
        GlStateManager.translate(-this.tire1.offsetX, -this.tire1.offsetY, -this.tire1.offsetZ);
        GlStateManager.translate(-this.tire1.rotationPointX * scale, -this.tire1.rotationPointY * scale, -this.tire1.rotationPointZ * scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.tire1.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.tire2.offsetX, this.tire2.offsetY, this.tire2.offsetZ);
        GlStateManager.translate(this.tire2.rotationPointX * scale, this.tire2.rotationPointY * scale, this.tire2.rotationPointZ * scale);
        GlStateManager.scale(0.9F, 0.9F, 1.0F);
        GlStateManager.translate(-this.tire2.offsetX, -this.tire2.offsetY, -this.tire2.offsetZ);
        GlStateManager.translate(-this.tire2.rotationPointX * scale, -this.tire2.rotationPointY * scale, -this.tire2.rotationPointZ * scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.tire2.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.tire3.offsetX, this.tire3.offsetY, this.tire3.offsetZ);
        GlStateManager.translate(this.tire3.rotationPointX * scale, this.tire3.rotationPointY * scale, this.tire3.rotationPointZ * scale);
        GlStateManager.scale(0.9F, 0.9F, 1.0F);
        GlStateManager.translate(-this.tire3.offsetX, -this.tire3.offsetY, -this.tire3.offsetZ);
        GlStateManager.translate(-this.tire3.rotationPointX * scale, -this.tire3.rotationPointY * scale, -this.tire3.rotationPointZ * scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.tire3.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.tire4.offsetX, this.tire4.offsetY, this.tire4.offsetZ);
        GlStateManager.translate(this.tire4.rotationPointX * scale, this.tire4.rotationPointY * scale, this.tire4.rotationPointZ * scale);
        GlStateManager.scale(0.9F, 0.9F, 1.0F);
        GlStateManager.translate(-this.tire4.offsetX, -this.tire4.offsetY, -this.tire4.offsetZ);
        GlStateManager.translate(-this.tire4.rotationPointX * scale, -this.tire4.rotationPointY * scale, -this.tire4.rotationPointZ * scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.tire4.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void setRotationAngles(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
}
