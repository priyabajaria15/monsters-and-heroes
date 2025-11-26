public class Weapon implements Item {

    private final String name;
    private final int cost;
    private final int level;
    private final int damage;   // base damage from the file
    private final int hands;    // how many hands it REQUIRES (1 or 2)

    // ✅ New: are we currently using this weapon with two hands?
    // Only meaningful if hands == 1 (one-handed weapon)
    private boolean usingTwoHands = false;

    public Weapon(String name, int cost, int level, int damage, int hands) {
        this.name = name;
        this.cost = cost;
        this.level = level;
        this.damage = damage;
        this.hands = hands;
    }

    // ============= BASIC GETTERS =============

    /** Base damage from the data file (no bonuses applied). */
    public int getDamage() {
        return damage;
    }

    /** How many hands this weapon *requires* (1 or 2). */
    public int getHands() {
        return hands;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public int getRequiredLevel() {
        return level;
    }

    // ============= TWO-HANDED USE LOGIC =============

    /**
     * Mark this weapon as being used with two hands.
     * Only has an effect if hands == 1 (one-handed weapon).
     */
    public void setUsingTwoHands(boolean usingTwoHands) {
        this.usingTwoHands = usingTwoHands;
    }

    /** Are we currently using this weapon with two hands? */
    public boolean isUsingTwoHands() {
        return usingTwoHands;
    }

    /**
     * Effective damage:
     *  - If the weapon is one-handed (hands == 1) AND usingTwoHands == true,
     *    we increase damage by 50% (you can change 1.5 to 2.0 if you want double).
     *  - Otherwise, we just return the base damage.
     */
    public int getEffectiveDamage() {
        if (hands == 1 && usingTwoHands) {
            return (int) Math.round(damage * 1.5);   // 50% bonus for two-handed use
        }
        return damage;
    }

    @Override
    public String toString() {
        // Show both base and effective damage so player can see the bonus.
        int effective = getEffectiveDamage();
        if (hands == 1 && usingTwoHands) {
            return String.format(
                    "%s (L%d, cost:%d, base dmg:%d, 2H dmg:%d, hands:%d, using two hands)",
                    name, level, cost, damage, effective, hands
            );
        } else {
            return String.format(
                    "%s (L%d, cost:%d, dmg:%d, hands:%d)",
                    name, level, cost, damage, hands
            );
        }
    }
}
