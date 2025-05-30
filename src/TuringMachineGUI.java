import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TuringMachineGUI {

    public static void main(String[] args){
        new TuringMachineGUI().mainHelper();
    }
    private TuringMachineParser parser = new TuringMachineParser();
    private TuringMachine machine;
    private JTextArea codeTextArea;
    private JTextArea logTextArea;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextArea expectedTextArea;


    private void mainHelper() {
        JFrame frame = new JFrame("Turing Machine Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        g
        frame.setSize(900, 500);

        // LEFT panel (left 50%) - includes text area and 3 buttons side by side
        JPanel leftPanel = createLeftPanel();

        // Top-right quarter
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.add(new JLabel("Console Log"), BorderLayout.NORTH);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFocusable(false);

        topRightPanel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

        // Bottom-right with 3 side-by-side text areas
        JPanel bottomRightPanel = new JPanel(new GridLayout(1, 3));
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(new JLabel("Input"), BorderLayout.NORTH);

        inputTextArea = new JTextArea();

        panel1.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
        bottomRightPanel.add(panel1);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(new JLabel("Output"), BorderLayout.NORTH);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setFocusable(false);

        panel2.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);
        bottomRightPanel.add(panel2);

        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(new JLabel("Expected"), BorderLayout.NORTH);

        expectedTextArea = new JTextArea();

        panel3.add(new JScrollPane(expectedTextArea), BorderLayout.CENTER);
        bottomRightPanel.add(panel3);

        // RIGHT side: split vertically between top-right and bottom-right
        JSplitPane rightVerticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topRightPanel, bottomRightPanel);
        rightVerticalSplit.setDividerLocation(0.5);
        rightVerticalSplit.setResizeWeight(0.5);

        // MAIN split: left and right
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightVerticalSplit);
        mainSplit.setDividerLocation(frame.getWidth()*4/7);
        mainSplit.setResizeWeight(0.5);

        frame.add(mainSplit);
        frame.setVisible(true);
    }

    // Helper method to create the left panel with a text area and 3 buttons side by side
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());

        leftPanel.add(new JLabel("Code"), BorderLayout.NORTH);

        // Create the text area at the top of the left panel
        codeTextArea = new JTextArea();
        JScrollPane leftScrollPane = new JScrollPane(codeTextArea);
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);

        // Create the button panel with 3 buttons side by side
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Buttons side by side
        JButton button1 = new JButton("Compile");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compile();
            }
        });
        JButton button2 = new JButton("Run");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });
        JButton button3 = new JButton("Compile and Run");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileAndRun();
            }
        });
        button1.setPreferredSize(new Dimension(150, 20));
        button2.setPreferredSize(new Dimension(150, 20));
        button3.setPreferredSize(new Dimension(150, 20));
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);

        // Add the button panel below the text area
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    // Helper method for creating a labeled text area
    private static JPanel labeledTextArea(String label, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(editable);
        if (!editable) {
            textArea.setFocusable(false); // no caret, no highlight
        }

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private boolean compileAndRun(){
        clearLog();
        if(!compileHelper()){
            return false;
        }
        if(!runHelper()) {
            return false;
        }
        return true;
    }

    private boolean compile() {
        clearLog();
        return compileHelper();
    }

    private boolean run() {
        clearLog();
        return runHelper();
    }

    private boolean compileHelper(){
        logMessage("Compiler", "Compilation has started.");
        String result = parser.parse(codeTextArea.getText());
        if(!result.isEmpty()){
            logMessage("Compiler", result);
            return false;
        }
        machine = new TuringMachine(codeTextArea.getText());
        logMessage("Compiler", "Compilation has finished.");
        return true;
    }

    private boolean runHelper(){
        logMessage("Runner", "Execution has started.");
        List<String> lines = inputTextArea.getText().lines().toList();
        outputTextArea.setText("");
        for(int i = 0; i < lines.size(); i++){
            outputTextArea.append(enumToString(machine.runTuringMachine(lines.get(i))));
            outputTextArea.append("\n");
        }
        List<String> outputLines = outputTextArea.getText().lines().toList();
        List<String> expectedLines = expectedTextArea.getText().lines().toList();
        if(outputLines.size() != expectedLines.size()){
            logMessage("Runner", "Expected " + expectedLines.size() + " lines of output but received " + outputLines.size() + ".");
            return false;
        }
        for(int i = 0; i < outputLines.size(); i++){
            if(!outputLines.get(i).equalsIgnoreCase(expectedLines.get(i))){
                logMessage("Runner", "Line " + (i + 1) + ": expected " + expectedLines.get(i).toUpperCase() + " but received " + outputLines.get(i).toUpperCase() + ".");
                return false;
            }
        }
        logMessage("Runner", "Execution has finished.");
        return true;
    }

    private static String enumToString(TuringMachine.RunResult result){
        if(result == TuringMachine.RunResult.REJECT){
            return("REJECT");
        }else if(result == TuringMachine.RunResult.ACCEPT){
            return("ACCEPT");
        }else{
            return("LOOP");
        }
    }

    private void clearLog(){
        logTextArea.setText("");
    }

    private void logMessage(String messenger, String message){
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        logTextArea.append("[" + now + "] [" + messenger + "] " + message + "\n");
    }
}
