package uqac.dim.projetcartalogue;

public enum PokemonTypeColors {
     Psy(108, 61, 107),Dragon(85,74,54), Electric(193,161,73),Fire(163,76,67), Fighting(154,68,42) , Plant(146,162,55),Water(19, 126, 189), dark(38,39,44), fairy(168,50,98),Steel(115,118,127),  normal(134,128,130);


    private final int r;
    private final int g;
    private final int b;

    PokemonTypeColors(int red, int green, int blue) {
        this.r = red;
        this.g = green;
        this.b = blue;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
