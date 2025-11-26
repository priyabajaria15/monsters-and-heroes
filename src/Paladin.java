public class Paladin extends Hero {
    public Paladin(String name, int level, int mana, int strength, int dexterity,
                   int agility, int gold, int experience) {
        super(name, level, mana, strength, dexterity, agility, gold, experience);
    }

    @Override
    protected void applyFavoredStatsOnLevelUp() {
        // Paladins favored: strength, dexterity :contentReference[oaicite:16]{index=16}
        strength = (int) (strength * 1.05);
        dexterity = (int) (dexterity * 1.05);
    }

    @Override
    public Hero copy() {
        Paladin p = new Paladin(name, level, mana, strength, dexterity, agility, gold, experience);
        p.hp = this.hp;
        return p;
    }
}
