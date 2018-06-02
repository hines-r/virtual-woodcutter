package com.moemeido.game.utils;

public class LevelScaling {

    private LevelScaling(){}

    public static int computeExperiencePoints(int level, float growthModifier) {
        return (int)((level * 100) * (level * growthModifier));
    }

    public static int computeUpgradeYields(int level, int base, float growthModifier) {
        return (int)((level * base) * (level * growthModifier));
    }

    // current = base * multiplier^level
    public static int calculateGrowth(float base, float multiplier, int level){
        return (int)(base * (float) Math.pow(multiplier, level));
    }

}
