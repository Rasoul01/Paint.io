package net.packet;

import model.HumanPlayer;

import java.io.Serial;
import java.io.Serializable;

public record InputPacket(HumanPlayer player, int keycode) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
