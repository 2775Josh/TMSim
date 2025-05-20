import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuringMachineParser {

    private static class ParseException extends Exception {
        public ParseException(String message){
            super(message);
        }
    }

    public String initState = null;
    public String acceptState = null;
    public Map<Observation, Transition> DFA = new HashMap<>();

    private String initLineCheck(String line){
        if(line.startsWith("init:")){
            return line.substring(5).strip();
        }else{
            return null;
        }
    }

    private String acceptLineCheck(String line){
        if(line.startsWith("accept:")){
            return line.substring(7).strip();
        }else{
            return null;
        }
    }

    public String parse(String inputText){
        initState = null;
        acceptState = null;
        DFA = new HashMap<>();
        List<String> lines = inputText.lines().toList();
        try {
            for(int lineno = 1; lineno <= lines.size(); lineno++){
                String line = lines.get(lineno-1);
                if(initLineCheck(line) != null){
                    if(initState != null){
                        throw new ParseException("Multiple init state assignment at line " + lineno + ".");
                    }
                    initState = initLineCheck(line);
                }else if(acceptLineCheck(line) != null){
                    if(acceptState != null){
                        throw new ParseException("Multiple accept state assignment at line " + lineno + ".");
                    }
                    acceptState = acceptLineCheck(line);
                }else {
                    String[] split = line.split(",");
                    for(int i = 0; i < split.length; i++){
                        split[i] = split[i].strip();
                    }
                    if (split.length != 2) {
                        continue;
                    }
                    if (split[1].length() != 1) {
                        throw new ParseException("Element must be single character at line " + lineno + ".");
                    }
                    Observation curObservation = new Observation(split[0], split[1].charAt(0));

                    line = lines.get(lineno++);
                    split = line.split(",");
                    for(int i = 0; i < split.length; i++){
                        split[i] = split[i].strip();
                    }
                    if (split.length != 3) {
                        throw new ParseException("Wrong number of elements at line " + lineno + ".");
                    }
                    if (split[1].length() != 1 || split[2].length() != 1) {
                        throw new ParseException("Element must be single character at line " + lineno + ".");
                    }
                    if (split[2].charAt(0) != '>' && split[2].charAt(0) != '<' && split[2].charAt(0) != '-') {
                        throw new ParseException("Invalid transition at line " + lineno + ".");
                    }
                    Transition curTransition = new Transition(split[0], split[1].charAt(0), split[2].charAt(0));
                    DFA.put(curObservation, curTransition);
                }
            }

            if(initState == null){
                throw new ParseException("No init state.");
            }
            if(acceptState == null){
                throw new ParseException("No accept state.");
            }
        } catch (ParseException e) {
            return("Parse error: " + e.getMessage());
        }
        return("");
    }
}
