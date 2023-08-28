package jcn.kwampaclan;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

public class LuckpPerms {
    private LuckPerms luckPerms;
    public LuckpPerms(LuckPerms luckPerms){
        this.luckPerms = luckPerms;
    }
    public void removePerm(Player player, String permission) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (permission.equals("clan.member")) {
            user.data().remove(Node.builder(permission).build());
        }
        if (permission.equals("clan.creator")) {
            user.data().remove(Node.builder(permission).build());
        }
        luckPerms.getUserManager().saveUser(user);
    }

    public void addPerm(Player player, String permission) {
        User user = this.luckPerms.getUserManager().getUser(player.getUniqueId());
        if (permission.equals("clan.member")) {
            user.data().add(Node.builder(permission).build());
        }
        if (permission.equals("clan.creator")) {
            user.data().add(Node.builder(permission).build());
        }
        this.luckPerms.getUserManager().saveUser(user);
    }
}
