package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class StorageMutatorScreen extends Screen {
    public StorageMutatorScreen(Component component) {
        super(component);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int x, int y, float partialTick) {
        super.render(poseStack, x, y, partialTick);
        int centerX = width / 2;
        int centerY = height / 2;
        GuiComponent.drawCenteredString(poseStack, font, "cX: " + centerX + ", cY: " + centerY, centerX, centerY, 0x00FF00);
        double angle = this.atan(y - centerY, x - centerX);
        //angle = (angle * 180 / Math.PI) % 360;
        GuiComponent.drawString(poseStack, font, "x: " + x + ", y: " + y + ", ang: " + angle, x, y, 0xFF0000);
    }

    private double atan(double opposite, double adjacent) {
        if (adjacent == 0) {
            adjacent = 1;
        }
        return Math.toDegrees(Math.atan(Math.abs(opposite / adjacent)));
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
            int centerX = width / 2;
            int centerY = height / 2;
            double angle = Math.tan(Math.abs(y - centerY) / Math.abs(x - centerX));
            System.out.println(angle);
            //this.onClose();
        }
        return true;
    }
}
