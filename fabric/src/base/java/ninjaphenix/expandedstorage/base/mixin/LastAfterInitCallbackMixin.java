package ninjaphenix.expandedstorage.base.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import ninjaphenix.expandedstorage.base.client.menu.PagedScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;

@Mixin(value = Screen.class, priority = 1001)
public abstract class LastAfterInitCallbackMixin {
    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("TAIL"))
    private void afterInit(CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PagedScreen screen && screen.hasPages()) {
            screen.children().forEach(System.out::println);
            int width = 54;
            int x = screen.getLeftPos() + screen.getImageWidth() - 61;
            int y = screen.getTopPos() + screen.getImageHeight() - 96;
            var renderableChildren = new ArrayList<AbstractWidget>();
            for (var widget : screen.children()) {
                if (widget instanceof AbstractWidget renderableWidget) {
                    renderableChildren.add(renderableWidget);
                }
            }
            renderableChildren.sort(Comparator.comparingInt(a -> -a.x));
            for (var widget : renderableChildren) {
                if (this.regionIntersects(widget, x, y, width, 12)) {
                    x = widget.x - width - 2;
                }
            }
            screen.createPageButtons(x, y);
        }
    }

    private boolean regionIntersects(AbstractWidget widget, int x, int y, int width, int height) {
        int wL = widget.x;
        int wR = wL + widget.getWidth();
        int wT = widget.y;
        int wB = wT + widget.getHeight();
        int bL = x;
        int bR = bL + width;
        int bT = y;
        int bB = bT + height;
        boolean lI = (bL <= wL && wL <= bR);
        boolean rI = (bL <= wR && wR <= bR);
        boolean tI = (bT <= wT && wT <= bB);
        boolean bI = (bT <= wB && wB <= bB);
        return lI && tI || rI && tI || lI && bI || rI && bI;
    }
}
