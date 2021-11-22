package it.kayes.universalenchant.main;

import com.vk2gpz.tokenenchant.api.InvalidTokenEnchantException;
import com.vk2gpz.tokenenchant.api.PotionHandler;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class UniversalEnchant extends PotionHandler {

    String defMaterial;

    public UniversalEnchant(TokenEnchantAPI plugin) throws InvalidTokenEnchantException {
        super(plugin);
        loadConfig();
    }

    public void loadConfig() {
        super.loadConfig();
        this.config = YamlConfiguration.loadConfiguration(new File("plugins/TokenEnchant/enchants" + File.separator + "Universal_config.yml"));
        System.out.println(this.config.getRoot());
        this.alias = ChatColor.translateAlternateColorCodes('&',this.config.getString("Potions.Universal.alias"));
        this.description = ChatColor.translateAlternateColorCodes('&',this.config.getString("Potions.Universal.description"));
        HashMap<String,EventPriority> event = new HashMap<String,EventPriority>();
        for (String s : this.config.getConfigurationSection("Potions.Universal.event_map").getKeys(false))
            event.put(s, EventPriority.valueOf(this.config.getString("Potions.Universal.event_map."+s)));
        this.eventPriorityMap = event;
        this.price = this.config.getDouble("Potions.Universal.price");
        this.max = this.config.getInt("Potions.Universal.max");
        this.occurrence = 100;
        this.defMaterial = this.config.getString("Potions.Universal.defaultMaterial");

        if (defMaterial==null) defMaterial = "NETHERITE";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    public String getName() {
        return "Universal";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        int level = getCELevel(p.getInventory().getItemInMainHand());

        if (level < 1 || !canExecute(p, level) || !checkCooldown(p))
            return;

        String it = functions.getTool(e.getClickedBlock().getType());
        ItemStack item = p.getInventory().getItemInMainHand();

        if (it == null || it.equalsIgnoreCase("SWORD"))
            return;

        if (it.equalsIgnoreCase("SHEARS")) {
            item.setType(Material.SHEARS);
        }else{
            item.setType(Material.valueOf(defMaterial.toUpperCase()+"_"+it));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(EntityDamageByEntityEvent e) {
        Entity en = e.getDamager();

        if (en instanceof Player) {
            Player p = (Player) en;

            int level = getCELevel(p.getInventory().getItemInMainHand());

            if (level < 1 || !canExecute(p, level) || !checkCooldown(p))
                return;

            ItemStack item = p.getInventory().getItemInMainHand();

            item.setType(Material.valueOf(defMaterial.toUpperCase()+"_SWORD"));
        }

    }


}