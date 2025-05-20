import java.util.Objects;

public class Observation {
    public final String currentState;
    public final Character currentSymbol;

    public Observation(String state, Character character){
        this.currentState = state;
        this.currentSymbol = character;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Observation o)) return false;
        return currentState.equals(o.currentState) && currentSymbol.equals(o.currentSymbol);
    }

    @Override
    public int hashCode(){
        return Objects.hash(currentState, currentSymbol);
    }
}