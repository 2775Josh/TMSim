import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TuringMachineTester {
    public static void main(String[] args) {
        if(args.length != 3){
            System.err.println("Usage: java TuringMachineTester [Turing Machine File] [Input File] [Output File]");
            System.exit(1);
        }
        String content = null;
        try {
            content = Files.readString(Path.of(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        TuringMachine machine = new TuringMachine(content);

        ArrayList<String> inputStrings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(args[1]))){
            String line;
            while((line = reader.readLine()) != null) {
                inputStrings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> outputStrings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(args[2]))){
            String line;
            while((line = reader.readLine()) != null) {
                outputStrings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(outputStrings.size() != inputStrings.size()){
            System.err.println("Mismatch in input and output file line counts");
            System.exit(1);
        }
        for(int i = 0; i < outputStrings.size(); i++){
            TuringMachine.RunResult result = machine.runTuringMachine(inputStrings.get(i));
            String actual = "LOOP";
            if(result == TuringMachine.RunResult.REJECT){
                actual = "REJECT";
            }else if(result == TuringMachine.RunResult.ACCEPT){
                actual = "ACCEPT";
            }
            String expected = outputStrings.get(i).toUpperCase();
            if(!actual.equals(expected)){
                System.err.println("Mismatch on testcase " + (i+1) + ". Expected " + expected + " but got " + actual + ".");
                System.exit(1);
            }
        }
        System.out.println("check passed");
    }
}