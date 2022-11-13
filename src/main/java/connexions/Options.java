/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package connexions;

/**
 * Enum to have a fast acces to petitions code
 *
 * @author NeRooN
 */
public enum Options {
    REGISTER((byte) 0),
    LOGIN((byte) 1),
    VIDEOGAMES_PAGINATION((byte) 2),
    INITIALIZATION((byte) 3),
    EDIT_USER((byte) 4);

    private byte value;

    Options(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
