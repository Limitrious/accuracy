package com.limitrious.plugins.accuracy;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Accuracy extends JavaPlugin implements Listener {
    private Material crossbow = null;
    private Enchantment multishot = null;

    // Configuration options
    private boolean disableMultishotAccuracy;
    private boolean disableGlobalAccuracy;

    // Random instance for applying spread
    private final Random random = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Save default config if not present
        disableMultishotAccuracy = getConfig().getBoolean("disable-multishot-accuracy", false);
        disableGlobalAccuracy = getConfig().getBoolean("disable-global-accuracy", false);

        getServer().getPluginManager().registerEvents(this, this);

        // Register the /accuracy command
        getCommand("accuracy").setExecutor((sender, command, label, args) -> {
            if (args.length < 2) {
                sender.sendMessage("Usage: /accuracy <global|multishot> <true|false>");
                return true;
            }

            String type = args[0].toLowerCase();
            String value = args[1].toLowerCase();

            if (!value.equals("true") && !value.equals("false")) {
                sender.sendMessage("Invalid value. Use 'true' or 'false'.");
                return true;
            }

            boolean state = Boolean.parseBoolean(value);

            switch (type) {
                case "global":
                    disableGlobalAccuracy = !state; // `true` means global accuracy is enabled
                    sender.sendMessage("Global accuracy is now " + (state ? "enabled" : "disabled") + "!");
                    break;

                case "multishot":
                    disableMultishotAccuracy = !state; // `true` means multishot accuracy is enabled
                    sender.sendMessage("Accuracy for multishot crossbows is now " + (state ? "enabled" : "disabled") + "!");
                    break;

                default:
                    sender.sendMessage("Invalid type. Use 'global' or 'multishot'.");
                    return true;
            }

            return true;
        });

        // Set the tab completer for /accuracy
        getCommand("accuracy").setTabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                // Suggest "global" and "multishot" for the first argument
                return Arrays.asList("global", "multishot").stream()
                        .filter(option -> option.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                // Suggest "true" and "false" for the second argument
                return Arrays.asList("true", "false").stream()
                        .filter(option -> option.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }

            return null; // No suggestions for additional arguments
        });

        // Register reload command
        getCommand("accuracyreload").setExecutor((sender, command, label, args) -> {
            reloadConfig();
            disableMultishotAccuracy = getConfig().getBoolean("disable-multishot-accuracy", false);
            disableGlobalAccuracy = getConfig().getBoolean("disable-global-accuracy", false);
            sender.sendMessage("Accuracy configuration reloaded!");
            return true;
        });

        try {
            crossbow = Material.CROSSBOW;
            multishot = Enchantment.MULTISHOT;
        } catch (Exception e) {
            crossbow = null;
            multishot = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileFire(ProjectileLaunchEvent event) {
        if (disableGlobalAccuracy) {
            // If global accuracy is disabled, skip all adjustments
            return;
        }

        ProjectileSource source = event.getEntity().getShooter();
        if (source instanceof Player) {
            Player player = (Player) source;

            // Check if the player is using a multishot crossbow
            boolean isMultishot = isMultishotCrossbow(player.getInventory().getItemInMainHand())
                    || isMultishotCrossbow(player.getInventory().getItemInOffHand());

            if (isMultishot) {
                if (disableMultishotAccuracy) {
                    // If multishot accuracy is disabled, apply default spread
                    applyDefaultMultishotSpread(event.getEntity());
                } else {
                    // Adjust multishot accuracy for 3 distinct arrows
                    adjustMultishotAccuracy(event.getEntity(), player);
                }
            } else {
                // Apply accuracy adjustments for non-multishot projectiles
                adjustProjectileAccuracy(event.getEntity(), player.getEyeLocation().getDirection());
            }
        }
    }

    private boolean isMultishotCrossbow(ItemStack item) {
        if (item == null || crossbow == null || multishot == null) {
            return false;
        }
        return item.getType() == crossbow && item.getEnchantmentLevel(multishot) > 0;
    }

    private void adjustMultishotAccuracy(Projectile projectile, Player player) {
        // Adjust multishot to ensure 3 distinct lines
        Vector baseDirection = player.getEyeLocation().getDirection().normalize();
        double spreadAngle = 10; // Spread angle in degrees

        // Calculate the left and right spread
        Vector leftDirection = rotateVector(baseDirection, -spreadAngle);
        Vector rightDirection = rotateVector(baseDirection, spreadAngle);

        // Assign the projectile to one of the three directions
        if (random.nextInt(3) == 0) {
            projectile.setVelocity(baseDirection.multiply(projectile.getVelocity().length()));
        } else if (random.nextInt(2) == 0) {
            projectile.setVelocity(leftDirection.multiply(projectile.getVelocity().length()));
        } else {
            projectile.setVelocity(rightDirection.multiply(projectile.getVelocity().length()));
        }
    }

    private void adjustProjectileAccuracy(Projectile projectile, Vector direction) {
        // Ensure projectiles like bow arrows are fully accurate
        projectile.setVelocity(direction.clone().normalize().multiply(projectile.getVelocity().length()));
    }

    private void applyDefaultMultishotSpread(Projectile projectile) {
        // Apply a slight random spread to simulate vanilla multishot behavior
        Vector velocity = projectile.getVelocity();
        double spread = 0.1; // Adjust this value to fine-tune the spread
        velocity.add(new Vector(
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread
        ));
        projectile.setVelocity(velocity);
    }

    private Vector rotateVector(Vector vector, double angle) {
        // Rotate a vector around the Y-axis by a given angle (in degrees)
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;
        return new Vector(x, vector.getY(), z);
    }
}