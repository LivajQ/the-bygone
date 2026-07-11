package com.jamiedev.bygone.client.renderer.entity;

import com.jamiedev.bygone.client.JamiesModModelLayers;
import com.jamiedev.bygone.client.models.BigBeakModel;
import com.jamiedev.bygone.common.entity.BigBeakEntity;
import com.jamiedev.bygone.common.item.CustomAnimalArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BigBeakArmorFeatureRenderer extends RenderLayer<BigBeakEntity, BigBeakModel<BigBeakEntity>> {
    private final BigBeakModel<BigBeakEntity> model;


    HorseArmorLayer ref;

    public BigBeakArmorFeatureRenderer(RenderLayerParent<BigBeakEntity, BigBeakModel<BigBeakEntity>> context, EntityModelSet loader) {
        super(context);
        this.model = new BigBeakModel<>(loader.bakeLayer(JamiesModModelLayers.BIG_BEAK_ARMOR));
    }
    
    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, BigBeakEntity bigBeakEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = bigBeakEntity.getItemBySlot(EquipmentSlot.CHEST);
        Item item = itemStack.getItem();
        if (item instanceof CustomAnimalArmorItem animalArmorItem) {
            if (animalArmorItem.getBodyType() == CustomAnimalArmorItem.BodyType.BIG_BEAK) {
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.prepareMobModel(bigBeakEntity, f, g, h);
                this.model.setupAnim(bigBeakEntity, f, g, j, k, l);
                
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutoutNoCull(animalArmorItem.getTexture()));
                this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
