package ninjaphenix.expandedstorage.chest.client;

public class BackChestModel extends SingleChestModel {
    public BackChestModel() {
        super(48, 48);
        lid.addBox(0, 0, 0, 14, 5, 15, 0);
        lid.setPos(1, 9, 1);
        base.texOffs(0, 20);
        base.addBox(0, 0, 0, 14, 10, 15, 0);
        base.setPos(1, 0, 1);
    }
}
