package org.usfirst.ftc.exampleteam.yourcodehere;

/**
 * Created by Austin on 1/16/2016.
 * this class is made so that we can pass a boolean in a field and modify it from within the method
 */
public class ModifiedBoolean {
    private boolean bool;

    public ModifiedBoolean(boolean b) {bool = b;}

    public ModifiedBoolean() {bool = false;}

    public void toggle() {bool = !bool;}

    public void setFalse() {bool = false;}

    public void setTrue() {bool = true;}

    public boolean equals(boolean b) {return bool == b;}

    public boolean getValue() {return bool;}
}
