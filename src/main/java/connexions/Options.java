/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package connexions;

/**
 *
 * @author NeRooN
 */
public enum Options {
    REGISTER((byte) 0),
    LOGIN((byte) 1),
    EDIT_USER((byte) 2);

    private byte value;

    Options(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
