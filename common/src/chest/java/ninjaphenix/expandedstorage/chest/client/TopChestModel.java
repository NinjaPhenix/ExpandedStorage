package ninjaphenix.expandedstorage.chest.client;

public final class TopChestModel extends SingleChestModel {
    public TopChestModel() {
        super(64, 48);
        lid.addBox(0, 0, 0, 14, 5, 14, 0);
        lid.addBox(6, -2, 14, 2, 4, 1, 0);
        lid.setPos(1, 9, 1);
        base.addBox(0, 0, 0, 14, 10, 14, 0);
        base.setPos(1, 0, 1);
    }
}
