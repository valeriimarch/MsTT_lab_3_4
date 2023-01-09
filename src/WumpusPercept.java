public class WumpusPercept {
    private boolean stench;
    private boolean breeze;
    private boolean glitter;
    private boolean scream;

    private final String[] breatheVar = new String[]{"I feel breeze here. ", "There is a breeze. ", "It's a cool breeze here. "};
    private final String[] stenchVar = new String[]{"I feel stench here. ", "There is a stench. ", "It's a stench here. "};
    private final String[] glitterVar = new String[]{"I see glitter here. ", "There is a glitter. ", "It's a glitter here. "};
    private final String[] screamVar = new String[]{"I hear scream. ", "There is a scream. ", "It's a scream here. "};

    public WumpusPercept setStench() {
        stench = true;
        return this;
    }

    public WumpusPercept setBreeze() {
        breeze = true;
        return this;
    }

    public WumpusPercept setGlitter() {
        glitter = true;
        return this;
    }

    public WumpusPercept setScream() {
        scream = true;
        return this;
    }

    public boolean isStench() {
        return stench;
    }

    public boolean isBreeze() {
        return breeze;
    }

    public boolean isGlitter() {
        return glitter;
    }

    public boolean isScream() {
        return scream;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int ran = 0;
        if (breeze) {
            ran = (int) Math.floor(Math.random() * breatheVar.length);
            result.append(breatheVar[ran]);
        }
        if (stench) {
            ran = (int) Math.floor(Math.random() * stenchVar.length);
            result.append(stenchVar[ran]);
        }
        if (glitter) {
            ran = (int) Math.floor(Math.random() * glitterVar.length);
            result.append(glitterVar[ran]);
        }
        if (scream) {
            ran = (int) Math.floor(Math.random() * screamVar.length);
            result.append(screamVar[ran]);
        }
        return result.toString();
    }
}