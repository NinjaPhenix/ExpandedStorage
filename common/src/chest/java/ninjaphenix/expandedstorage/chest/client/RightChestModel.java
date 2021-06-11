package ninjaphenix.expandedstorage.chest.client;

public class RightChestModel extends SingleChestModel {
    public RightChestModel() {
        super(64, 48);
        lid.addBox(0, 0, 0, 15, 5, 14, 0);
        lid.addBox(0, -2, 14, 1, 4, 1, 0);
        lid.setPos(0, 9, 1);
        base.addBox(0, 0, 0, 15, 10, 14, 0);
        base.setPos(0, 0, 1);
    }
}
