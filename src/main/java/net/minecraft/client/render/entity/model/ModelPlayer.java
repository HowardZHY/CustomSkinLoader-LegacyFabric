package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class ModelPlayer extends BiPedModel
{
    public ModelPart /* bipedLeftArmwear */ field_178734_a;
    public ModelPart /* bipedRightArmwear */ field_178732_b;
    public ModelPart /* bipedLeftLegwear */ field_178733_c;
    public ModelPart /* bipedRightLegwear */ field_178731_d;
    public ModelPart /* bipedBodyWear */ field_178730_v;
    private final boolean /* smallArms */ field_178735_y;

    public ModelPlayer(float modelSize, boolean smallArmsIn)
    {
        super(modelSize, 0.0F, 64, 64);
        this.field_178735_y = smallArmsIn;
        this.field_1481 = new ModelPart(this, 0, 0);
        this.field_1481.setTextureSize(64, 32);
        this.field_1481.addCuboid(-5.0F, 0.0F, -1.0F, 10, 16, 1, modelSize);

        if (smallArmsIn)
        {
            this.field_1477 = new ModelPart(this, 32, 48);
            this.field_1477.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.field_1477.setPivot(5.0F, 2.5F, 0.0F);
            this.field_1476 = new ModelPart(this, 40, 16);
            this.field_1476.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.field_1476.setPivot(-5.0F, 2.5F, 0.0F);
            this.field_178734_a = new ModelPart(this, 48, 48);
            this.field_178734_a.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.field_178732_b = new ModelPart(this, 40, 32);
            this.field_178732_b.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
        }
        else
        {
            this.field_1477 = new ModelPart(this, 32, 48);
            this.field_1477.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.field_1477.setPivot(5.0F, 2.0F, 0.0F);
            this.field_178734_a = new ModelPart(this, 48, 48);
            this.field_178734_a.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.field_178732_b = new ModelPart(this, 40, 32);
            this.field_178732_b.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        }

        this.field_1479 = new ModelPart(this, 16, 48);
        this.field_1479.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.field_1479.setPivot(1.9F, 12.0F, 0.0F);
        this.field_178733_c = new ModelPart(this, 0, 48);
        this.field_178733_c.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.field_178731_d = new ModelPart(this, 0, 32);
        this.field_178731_d.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.field_178730_v = new ModelPart(this, 16, 32);
        this.field_178730_v.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);

        this.field_1477.add(this.field_178734_a);
        this.field_1476.add(this.field_178732_b);
        this.field_1479.add(this.field_178733_c);
        this.field_1478.add(this.field_178731_d);
        this.body.add(this.field_178730_v);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        if (this.field_178735_y)
        {
            this.field_1476.pivotX += 1.0F;
        }
    }
}