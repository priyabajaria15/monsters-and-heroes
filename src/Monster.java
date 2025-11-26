/**
 * Monster class represents enemies in the game.
 * Each Monster has damage, defense, and dodge chance.
 */
public abstract class Monster extends Living {
    protected int baseDamage;    // Base attack damage
    protected int defense;       // Reduces incoming damage
    protected int dodgeChance;   // Stored as percentage (0-100), e.g., 25 = 25% chance to dodge

    public Monster(String name, int level, int baseDamage, int defense, int dodgeChance) {
        super(name, level);  // Calls Living constructor (sets name, level, hp)

        // Ensure valid values (never negative or beyond limits)
        this.baseDamage = Math.max(0, baseDamage);
        this.defense = Math.max(0, defense);
        this.dodgeChance = clampToPercent(dodgeChance); // Keep between 0–100
    }

    public abstract Monster copy();  // Must be implemented in child classes

    // Getter & Setter for Damage
    public int getBaseDamage() { return baseDamage; }
    public void setBaseDamage(int baseDamage) {
        this.baseDamage = Math.max(0, baseDamage); // Prevent negative damage
    }

    // Getter & Setter for Defense
    public int getDefense() { return defense; }
    public void setDefense(int defense) {
        this.defense = Math.max(0, defense); // Prevent negative defense
    }

    /**
     * Convert dodge chance from % to actual probability.
     * Example: 25% → 0.25, used for random dodge calculations.
     *
     * We also CAP it at 30% so monsters don't dodge nearly everything.
     */
    public double dodgeChance() {
        double prob = dodgeChance * 0.01;  // convert from percent to [0,1]
        return Math.min(0.30, prob);       // cap at 30%
    }

    /**
     * 🔹 Reduces dodge chance by a given percentage.
     * Example: percent = 0.10 → reduce dodge by 10%.
     * Always keeps dodgeChance between 0 and 100.
     */
    public void reduceDodgeChanceByPercent(double percent) {
        double factor = 1.0 - percent; // E.g., 0.9 for 10% reduction
        int newChance = (int) Math.round(dodgeChance * factor);
        dodgeChance = clampToPercent(newChance); // Keep in valid range
    }

    /**
     * 🔹 Convenience method for Lightning Spell.
     * Always reduces dodge by 10% (as per game rules).
     */
    public void reduceDodgeChance() {
        reduceDodgeChanceByPercent(0.10); // 10% reduction
    }

    /**
     * Utility to keep any % value within valid range:
     * Always between 0 and 100 (inclusive).
     */
    private int clampToPercent(int value) {
        return Math.max(0, Math.min(100, value));
    }

    @Override
    public String toString() {
        return String.format(
                "%s (L%d, HP:%d, Dmg:%d, Def:%d, Dodge:%d%%)",
                name, level, hp, baseDamage, defense, dodgeChance
        );
    }
}
