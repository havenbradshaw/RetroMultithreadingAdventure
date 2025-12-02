package core;

/**
 * Immutable configuration for a game run.
 */
public final class GameConfig {
    public final int totalParts;
    public final int roundsPerPart;
    public final String knightName;
    public final String wizardName;
    public final String thiefName;
    public final String latestAdvice;

    public GameConfig(int totalParts, int roundsPerPart, String knightName, String wizardName, String thiefName, String latestAdvice) {
        this.totalParts = totalParts;
        this.roundsPerPart = roundsPerPart;
        this.knightName = knightName;
        this.wizardName = wizardName;
        this.thiefName = thiefName;
        this.latestAdvice = latestAdvice;
    }
}
