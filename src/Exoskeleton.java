public class Exoskeleton extends Monster {
    public Exoskeleton(String name, int level, int baseDamage, int defense, int dodgeChance) {
        super(name, level, baseDamage, defense, dodgeChance);
    }

    @Override
    public Monster copy() {
        Exoskeleton e = new Exoskeleton(name, level, baseDamage, defense, dodgeChance);
        e.hp = this.hp;
        return e;
    }
}
