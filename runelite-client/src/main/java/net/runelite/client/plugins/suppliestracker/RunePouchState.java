package net.runelite.client.plugins.suppliestracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@AllArgsConstructor
public class RunePouchState {
    @Getter
    private int[] amount;

    @Getter
    private int[] ids;

    @Getter
    private long time;

    public RunePouchState(){
        amount = new int[3];
        ids = new int[3];
        time = 0;
    }

    public static boolean equal_pouches(RunePouchState pouch1, RunePouchState pouch2){
        for (int i = 0; i < 3; i++){
            if (pouch1.getAmount()[i] != pouch2.getAmount()[i]){
                return false;
            }
            if (pouch1.getIds()[i] != pouch2.getIds()[i]){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString(){
        return ("Amounts: " + Arrays.toString(amount) + " IDs: " + Arrays.toString(ids));
    }
}
