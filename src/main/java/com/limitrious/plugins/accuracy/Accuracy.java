package com.darkender.plugins.accuracy;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class Accuracy extends JavaPlugin implements Listener {
    private Material crossbow = null;
    private Enchantment multishot = null;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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
        ProjectileSource source = event.getEntity().getShooter();
        if (source instanceof Player) {
            Player player = (Player) source;
            if (isMultishotCrossbow(player.getInventory().getItemInMainHand()) || 
                isMultishotCrossbow(player.getInventory().getItemInOffHand())) {

                Vector velocityDirection = event.getEntity().getVelocity().normalize();
                Vector head = getVectorFromPitch(
                    player.getEyeLocation().getPitch() - 90.0F, 
                    player.getEyeLocation().getYaw()
                ).normalize();

                Vector right = player.getEyeLocation().getDirection().clone();
                OldVersionCompatibility.rotateAroundNonUnitAxis(right, head, -10 * (Math.PI / 180.0));
                double current = event.getEntity().getVelocity().length();

                if (right.distanceSquared(velocityDirection) < 0.001) {
                    event.getEntity().setVelocity(right.multiply(current));
                } else {
                    Vector left = player.getEyeLocation().getDirection().clone();
                    OldVersionCompatibility.rotateAroundNonUnitAxis(left, head, 10 * (Math.PI / 180.0));
                    if (left.distanceSquared(velocityDirection) < 0.001) {
                        event.getEntity().setVelocity(left.multiply(current));
                    } else {
                        event.getEntity().setVelocity(player.getEyeLocation().getDirection().multiply(current));
                    }
                }
            } else {
                fixVelocity(event.getEntity(), player.getEyeLocation().getDirection());
            }
        } else if (source instanceof BlockProjectileSource) {
            BlockFace face = ((BlockProjectileSource) source).getBlock().getFace(event.getEntity().getLocation().getBlock());
            fixVelocity(event.getEntity(), OldVersionCompatibility.getBlockFaceDirection(face));
        }
    }

    private Vector getVectorFromPitch(float pitch, float yaw) {
        double pitchRad = Math.toRadians(pitch);
        double yawRad = -Math.toRadians(yaw);
        return new Vector(
            Math.sin(yawRad) * Math.cos(pitchRad), 
            -Math.sin(pitchRad), 
            Math.cos(yawRad) * Math.cos(pitchRad)
        );
    }

    private boolean isMultishotCrossbow(ItemStack item) {
        if (item == null || crossbow == null || multishot == null) {
            return false;
        }
        return item.getType() == crossbow && item.getEnchantmentLevel(multishot) > 0;
    }

    private void fixVelocity(Entity entity, Vector direction) {
        entity.setVelocity(direction.clone().normalize().multiply(entity.getVelocity().length()));
    }
}
