package net.runelite.client.plugins.gauntlethelper;

import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public class GameTile {
    private int x;
    private int y;

    public GameTile(WorldPoint w){
        x = w.getX();
        y = w.getY();
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31*result + x;
        result = 31*result + y;
        return result;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (!(o instanceof GameTile)){
            return false;
        }
        if (((GameTile)o).x == x && ((GameTile) o).y == y){
            return true;
        }
        return false;
    }

    public String printTile(){
        return x + " " + y;
    }
}

