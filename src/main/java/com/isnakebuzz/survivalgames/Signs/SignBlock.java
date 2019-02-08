package com.isnakebuzz.survivalgames.Signs;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Signs.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class SignBlock {

    private String location;
    private boolean rotation;
    private boolean updating;
    private Arena arena;
    private Block retract;

    public SignBlock(String location) {
        this.location = location;
        this.arena = null;
        this.rotation = false;
        this.retract = getBlockFaced(LocationUtil.getLocation(location).getBlock());
    }

    public Sign getSign() {
        final Location location = LocationUtil.getLocation(this.location);
        if (location != null) {
            final Block block = location.getWorld().getBlockAt(location);
            if (block != null && (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
                return (Sign) block.getState();
            }
        }
        return null;
    }

    public String getLocation() {
        return this.location;
    }

    public boolean isRotation() {
        return this.rotation;
    }

    public void setRotation(final boolean rotation) {
        this.rotation = rotation;
    }

    public boolean isUpdating() {
        return this.updating;
    }

    public void setUpdating(final boolean updating) {
        this.updating = updating;
    }

    public Arena getGame() {
        return arena;
    }

    public void setGame(Arena arena) {
        this.arena = arena;
    }

    public void updateBlock() {
        this.getRetract().setType(Material.STAINED_GLASS);
        if (this.arena != null) {
            this.getRetract().setData((byte) getGame().getBlockStatus());
        } else {
            this.getRetract().setData((byte) 0);
        }
        this.getRetract().getState().update(true);
    }

    public Block getBlockFaced(Block b) {
        switch (b.getData()) {
            case 2:
                return b.getRelative(BlockFace.SOUTH);
            case 3:
                return b.getRelative(BlockFace.NORTH);
            case 4:
                return b.getRelative(BlockFace.EAST);
            case 5:
                return b.getRelative(BlockFace.WEST);
            default:
                return b;
        }
    }

    public void setSignText(final Sign sign, final int n, final String s) {
        final int n2 = 15;
        if (null == s || null == sign) {
            throw new IllegalArgumentException("Sign or Text to set in sign was null");
        }
        if (n > 3) {
            throw new IllegalArgumentException("position was > than 3");
        }
        if (s.length() >= n2) {
            sign.setLine(n, s.substring(0, n2));
        } else {
            sign.setLine(n, s);
        }
        sign.update();
    }

    private Block getRetract() {
        return retract;
    }
}
