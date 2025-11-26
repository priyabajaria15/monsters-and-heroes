public class Spell implements Item {
    private final String name;
    private final int cost;
    private final int level;
    private final int damage;
    private final int manaCost;
    private final SpellType type;

    public Spell(String name, int cost, int level, int damage, int manaCost, SpellType type) {
        this.name = name;
        this.cost = cost;
        this.level = level;
        this.damage = damage;
        this.manaCost = manaCost;
        this.type = type;
    }

    public int getDamage() { return damage; }
    public int getManaCost() { return manaCost; }
    public SpellType getType() { return type; }

    @Override public String getName() { return name; }
    @Override public int getCost() { return cost; }
    @Override public int getRequiredLevel() { return level; }

    @Override
    public String toString() {
        return String.format("%s (%s, L%d, cost:%d, dmg:%d, MP:%d)",
                name, type, level, cost, damage, manaCost);
    }
}
