public class Sorcerer extends Hero {
    public Sorcerer(String name, int level, int mana, int strength, int dexterity,
                    int agility, int gold, int experience) {
        super(name, level, mana, strength, dexterity, agility, gold, experience);
    }

    @Override
    protected void applyFavoredStatsOnLevelUp() {
        // Sorcerers favored: dexterity, agility :contentReference[oaicite:15]{index=15}
        dexterity = (int) (dexterity * 1.05);
        agility = (int) (agility * 1.05);
    }

    @Override
    public Hero copy() {
        Sorcerer s = new Sorcerer(name, level, mana, strength, dexterity, agility, gold, experience);
        s.hp = this.hp;
        return s;
    }
}
