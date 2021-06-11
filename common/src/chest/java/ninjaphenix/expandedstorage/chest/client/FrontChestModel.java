package ninjaphenix.expandedstorage.chest.client;

public class FrontChestModel extends SingleChestModel {
    public FrontChestModel() {
        super(64, 48);
        lid.addBox(0, 0, 15, 14, 5, 15, 0);
        lid.addBox(6, -2, 30, 2, 4, 1, 0);
        lid.setPos(1, 9, -15);
        base.texOffs(0, 20);
        base.addBox(0, 0, 0, 14, 10, 15, 0);
        base.setPos(1, 0, 0);
    }
}
