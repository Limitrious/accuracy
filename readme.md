# Accuracy Plugin

### About
The **Accuracy** plugin is a lightweight and efficient tool for Minecraft servers that enhances projectile behavior. It ensures **precise projectile trajectories** for arrows, crossbows, and dispenser-fired projectiles, providing a smooth and consistent experience for players.

Updated for Minecraft **1.21.1** by **Limitrious**.

---

### Features
- **Multishot Crossbow Support**:
  - Handles the unique behavior of multishot crossbows by adjusting the trajectory of arrows to ensure accurate flight paths.
  - **NEW:** Allows for enabling or disabling accuracy adjustments for multishot crossbows:
    - When enabled, arrows are fired in 3 distinct lines with precise trajectories.
    - When disabled, arrows follow the default vanilla spread.

- **Projectile Velocity Correction**:
  - Corrects projectile velocity for more precise and consistent movement when fired by players or block sources.

- **Enhanced Projectile Accuracy**:
  - The following projectiles now have more precise trajectories:
    - Arrows from bows
    - Arrows from crossbows (including multishot)
    - Arrows from dispensers
    - Tridents
    - Splash/Lingering potions
    - Enderpearls
  - **NEW:** Fully accurate trajectories for bows and normal crossbows, ensuring no recoil or instability.

- **Advanced Multishot Arrow Behavior**:
  - **NEW:** Multishot crossbows now fire arrows with a controlled spread:
    - Arrows are evenly split into 3 lines: one straight, one to the left, and one to the right.
    - Spread behavior is consistent with vanilla mechanics while maintaining accuracy adjustments.

- **Compatibility**:
  - Seamlessly works with block-based projectile sources like dispensers.
  - Fully compatible with Minecraft 1.21.1 and older mechanics.

- **Advanced Projectile Handling**:
  - Automatically adjusts projectiles based on the shooter's direction, even considering block faces when fired from dispensers.

- **Legacy Support**:
  - Maintains compatibility with older Minecraft mechanics while supporting the latest features.

- **Lightweight and Efficient**:
  - Has minimal impact on server performance, even under heavy usage.

---

### New Commands and Tab Completion
- **NEW:** Added `/accuracy` command to toggle accuracy adjustments for projectiles:
  - `/accuracy <global|multishot> <true|false>`:
    - `global`: Toggles accuracy adjustments for all projectiles.
    - `multishot`: Toggles accuracy adjustments specifically for multishot crossbows.
  - Example usage:
    - `/accuracy global true` - Enables accuracy for all projectiles.
    - `/accuracy multishot false` - Disables accuracy for multishot crossbows.
- **NEW:** Tab completion support:
  - Suggests valid arguments for the `/accuracy` command.

---

### Installation
1. Download the `Accuracy.jar` file from the Modrinth page.
2. Place the `.jar` file in your server's `plugins` folder.
3. Restart or reload your server to activate the plugin.

---

### Configuration
- **NEW Configurable Options**:
  - `disable-global-accuracy`: Toggles accuracy adjustments for all projectiles.
  - `disable-multishot-accuracy`: Toggles accuracy adjustments specifically for multishot crossbows.

Example `config.yml`:
```yaml
# Configuration for the Accuracy plugin
disable-multishot-accuracy: false
disable-global-accuracy: false