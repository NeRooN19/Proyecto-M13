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
    EDIT_USER((byte) 4),
    EDIT_GAME((byte) 5),
    NEW_GAME((byte) 6),
    MAKE_ADMIN((byte) 7),
    NEW_RENTAL((byte) 8),
    NEW_SCORE((byte) 9),
    DELETE_GAME((byte) 10),
    GET_USER_LIST((byte) 11),
    UPDATE_USER_STATUS((byte) 12),
    GET_VIDEOGAME_LIST((byte) 13);


    private final byte value;

    Options(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
