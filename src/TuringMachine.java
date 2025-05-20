import java.util.Map;

public class TuringMachine {

    public enum RunResult {
        ACCEPT,
        REJECT,
        LOOP
    }

    private String initState;
    private String acceptState;

    private Map<Observation, Transition> DFA;

    private StringBuilder inputTape;
    private int curPosition;
    private String curState;

    public int loopCutoff = 1000;

    public TuringMachine(String inputText){
        TuringMachineParser parser = new TuringMachineParser();
        String result = parser.parse(inputText);
        if(!result.isEmpty()){
            System.err.println(result);
            System.exit(1);
        }
        this.initState = parser.initState;
        this.acceptState = parser.acceptState;
        this.DFA = parser.DFA;
    }

    private boolean makeTransition(){
        Observation curObservation = new Observation(curState, inputTape.charAt(curPosition));
        if(DFA.containsKey(curObservation)){
            Transition curTransition = DFA.get(curObservation);
            curState = curTransition.nextState();
            inputTape.setCharAt(curPosition, curTransition.writtenSymbol());
            if(curTransition.direction().equals('<')){
                if(curPosition > 0){
                    curPosition--;
                }
            }else if(curTransition.direction().equals('>')){
                if(++curPosition == inputTape.length()){
                    inputTape.append('_');
                }
            }
        }else{
            return false;
        }
        return true;
    }

    public RunResult runTuringMachine(String inputTape){
        this.inputTape = new StringBuilder(inputTape);
        this.curState = initState;
        this.curPosition = 0;
        for(int step = 0; step < this.loopCutoff; step++){
            if(!makeTransition()){
                if (curState.equals(acceptState)) {
                    return RunResult.ACCEPT;
                }
                return RunResult.REJECT;
            }
        }
        return RunResult.LOOP;
    }
}
