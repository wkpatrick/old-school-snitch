package xyz.wkrp.records;

public class UserLocation {
        private int x_coord;
        private int y_coord;
        private String playerId;

    public UserLocation(int xCoord, int yCoord, String playerId) {
        x_coord = xCoord;
        y_coord = yCoord;
        this.playerId = playerId;
    }
}
