package com.legendgamer.realism.client.render.tile;

import com.legendgamer.realism.tile.TileWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTranslated;

public class TileWorkbenchRenderer extends TileEntitySpecialRenderer<TileWorkbench> {

    @Override
    public void render(TileWorkbench te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        glPushMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F, 240F);

        glTranslated(x + 0.0625 * 5, y + 0.65F, z + 0.0625 * 5);

        for (int xi = 0; xi <= 2; xi++) {
            for (int yi = 0; yi <= 2; yi++) {
                ItemStack stackInSlot = te.inv.getStackInSlot(xi * 3 + yi).copy();

                glPushMatrix();
                glTranslated(xi * 0.0625 * 3, 0, yi * 0.0625 * 3);
                glScaled(0.5, 0.5, 0.5);
                int count = Math.min(stackInSlot.getCount(), 3);
                stackInSlot.setCount(1);
                Minecraft mc = Minecraft.getMinecraft();
                ItemRenderer ir = mc.getItemRenderer();
                glRotated(-90, 1, 0, 0);
                glTranslated(0, -0.0625 * 3, 0.3);
                if (!(stackInSlot.getItem() instanceof ItemBlock)) {
                    glTranslated(0, 0.0625, -0.0625);
                    for (int i = 2; i < count + 2; i++) {
                        glTranslated(0.05 * (i % 3 - 1), 0.05 * (i % 3 - 1), (0.008) * i);
                        ir.renderItem(mc.player, stackInSlot, ItemCameraTransforms.TransformType.GROUND);
                    }
                } else {
                    for (int i = 1; i < count+1; i++) {
                        glTranslated(0.05 * (i % 3 - 1), 0.05 * (i % 3 - 1), (0.008) * i);
                        ir.renderItem(mc.player, stackInSlot, ItemCameraTransforms.TransformType.GROUND);
                    }
                }
                //ir.renderItem(mc.player, stackInSlot, ItemCameraTransforms.TransformType.GROUND);
                glPopMatrix();
            }
        }
        glPopMatrix();
    }
}
