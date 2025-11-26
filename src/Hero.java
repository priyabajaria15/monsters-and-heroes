import java.util.ArrayList;
import java.util.List;

public abstract class Hero extends Living {
    protected int mana;
    protected int strength;
    protected int dexterity;
    protected int agility;
    protected int gold;
    protected int experience;

    protected Weapon weapon;
    protected Armor armor;
    protected final List<Item> inventory = new ArrayList<>();
    protected boolean fainted = false;

    // whether the hero is currently using their weapon with both hands
    protected boolean usingTwoHands = false;

    public Hero(String name, int level, int mana, int strength, int dexterity,
                int agility, int gold, int experience) {
        super(name, level);
        this.mana = mana;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
        this.gold = gold;
        this.experience = experience;
    }

    public abstract Hero copy();
    protected abstract void applyFavoredStatsOnLevelUp();

    public boolean isFainted() { return fainted; }
    public void setFainted(boolean f) { fainted = f; }

    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = mana; }

    public int getStrength() { return strength; }
    public int getDexterity() { return dexterity; }
    public int getAgility() { return agility; }
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public int getExperience() { return experience; }
    public List<Item> getInventory() { return inventory; }

    public Weapon getWeapon() { return weapon; }
    public Armor getArmor() { return armor; }

    // expose two-hand flag
    public boolean isUsingTwoHands() {
        return usingTwoHands;
    }

    public void setUsingTwoHands(boolean usingTwoHands) {
        this.usingTwoHands = usingTwoHands;
    }

    public void equipWeapon(Weapon w) {
        this.weapon = w;
        // when you switch weapons, default back to one-hand usage
        this.usingTwoHands = false;
    }

    public void equipArmor(Armor a) {
        this.armor = a;
    }

    /**
     * Hero’s dodge chance = agility × 0.002, but capped at 30%
     * so they don't dodge nearly everything when agility is high.
     */
    public double dodgeChance() {
        return Math.min(0.30, agility * 0.002);
    }

    /**
     * Hero’s attack damage (with weapon) = (strength + weapon_damage) × 0.05
     * If a one-handed weapon is used with both hands → boost weapon damage by 50%.
     * Also: ensure at least 1 damage so we don't get 0-damage hits.
     */
    public int weaponDamage() {
        int baseWeaponDmg = 0;

        if (weapon != null) {
            baseWeaponDmg = weapon.getDamage();

            // If it is a 1-handed weapon and hero is using both hands → increase damage
            if (weapon.getHands() == 1 && usingTwoHands) {
                baseWeaponDmg = (int) Math.round(baseWeaponDmg * 1.5);
            }
        }

        // use formula from spec
        double raw = (strength + baseWeaponDmg) * 0.05;
        int dmg = (int) Math.round(raw);

        // guarantee at least 1 damage
        return Math.max(1, dmg);
    }

    /**
     * Hero’s spell damage = spell_base_damage + (dexterity / 10000) × spell_base_damage
     */
    public int spellDamage(Spell s) {
        return (int) (s.getDamage() + (dexterity / 10000.0) * s.getDamage());
    }

    /**
     * Experience points to level up = hero_current_level × 10
     * Gold and EXP added, then levelUp() while enough EXP.
     */
    public void gainExpAndGold(int expGain, int goldGain) {
        experience += expGain;
        gold += goldGain;
        while (experience >= level * 10) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        hp = level * 100;                // HP = level × 100
        mana = (int) (mana * 1.1);       // MP = current_mana × 1.1
        strength = (int) (strength * 1.05);
        dexterity = (int) (dexterity * 1.05);
        agility = (int) (agility * 1.05);

        // favored stats get extra 5% in subclass
        applyFavoredStatsOnLevelUp();

        System.out.println(name + " leveled up! Now level " + level);
    }

    public String shortStats() {
        return String.format("%s (L%d, HP:%d, MP:%d, Str:%d, Dex:%d, Agi:%d, Gold:%d)",
                name, level, hp, mana, strength, dexterity, agility, gold);
    }

    @Override
    public String toString() {
        return shortStats() +
                String.format(" Exp:%d Weapon:%s Armor:%s",
                        experience,
                        weapon == null ? "None" : weapon.getName(),
                        armor == null ? "None" : armor.getName());
    }
}
