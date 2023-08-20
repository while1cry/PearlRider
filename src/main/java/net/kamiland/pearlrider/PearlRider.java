package net.kamiland.pearlrider;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PearlRider extends JavaPlugin implements Listener, TabExecutor {
    private static PearlRider instance;
    private static Map<Player, EnderPearl> map = new HashMap<>();
    private static Map<Player, Boolean> enabledMap = new HashMap<>();

    private static boolean ENABLED_BY_DEFAULT;
    private static boolean ANTI_TRAPPED;
    private static boolean CONSUMABLE;
    private static List<String> WORLD_LIST;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("pearlrider").setExecutor(this);
        Bukkit.getPluginCommand("pearlrider").setTabCompleter(this);

        Metrics metrics = new Metrics(getInstance(), 19579);

        Bukkit.getConsoleSender().sendMessage("§aPearlRider successfully enabled.");
    }

    private void loadConfig() {
        saveResource("config.yml", false);
        File file = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ENABLED_BY_DEFAULT = config.getBoolean("enabled-by-default");
        ANTI_TRAPPED = config.getBoolean("anti-trapped");
        CONSUMABLE = config.getBoolean("consumable");
        WORLD_LIST = config.getStringList("world-list");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c请以玩家的身份执行这个指令!");
            return false;
        }

        if (args == null || args.length < 1) {
            if (!sender.hasPermission("pearlrider.use")) {
                sender.sendMessage("§c你没有权限这样做!");
                return false;
            }

            enabledMap.put((Player) sender, !enabledMap.get((Player) sender));
            if (enabledMap.get((Player) sender)) {
                sender.sendMessage("§a你的珍珠骑乘模式已开启!");
            } else {
                sender.sendMessage("§c你的珍珠骑乘模式已关闭!");
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("pearlrider.reload")) {
                loadConfig();
                sender.sendMessage("§aPearlRider的配置文件已成功重载!");
                return true;
            } else {
                sender.sendMessage("§c你没有权限这样做!");
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        if (enabledMap.get(player) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && itemInHand != null && itemInHand.getType() == Material.ENDER_PEARL && WORLD_LIST.contains(player.getWorld().getName())) {
            EnderPearl pearl = player.launchProjectile(EnderPearl.class);
            pearl.setPassenger(player);
            e.setCancelled(true);

            if (!map.containsKey(player)) {
                map.put(player, pearl);
            } else {
                map.get(player).remove();
                map.put(player, pearl);
            }

            if (CONSUMABLE) {
                if (itemInHand.getAmount() == 1) {
                    player.getInventory().remove(itemInHand);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
                player.updateInventory();
            } else {
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("pearlrider.use") && !enabledMap.containsKey(e.getPlayer())) {
            enabledMap.put(e.getPlayer(), ENABLED_BY_DEFAULT);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (enabledMap.get(player)) {
            if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.ENDER_PEARL) {
                EnderPearl pearl = (EnderPearl) player.getVehicle();
                Vector velocity = pearl.getVelocity();
                pearl.setVelocity(velocity);
            } else if (map.containsKey(player)) {
                map.get(player).remove();
                map.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (ANTI_TRAPPED && e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && WORLD_LIST.contains(e.getPlayer().getWorld().getName())) {
            e.getPlayer().setVelocity(new Vector(0, 0.05, 0));
            map.remove(e.getPlayer());
        }
    }

    public static PearlRider getInstance() {
        return instance;
    }
}
