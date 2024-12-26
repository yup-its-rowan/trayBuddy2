package MidiKey;

import java.util.ArrayList;
import java.util.HashMap;


public class PatternInterpreter {
    private Node root;
    public static PatternInterpreter PatternInterpreterSingleton = new PatternInterpreter();

    private PatternInterpreter() {
        root = new Node();
        initializePatterns(patterns);
        //printNoteSearch(root);
    }


    private Pattern freddyPattern = new Pattern(new int[]{69, 71, 72}, (byte) 1);
    private Pattern christmasPattern = new Pattern(new int[]{64, 64, 64, 64, 64, 64, 64, 67, 60, 62, 64}, (byte) 2);
    private Pattern[] patterns = {freddyPattern, christmasPattern};

    private ArrayList<Node> currentNodes = new ArrayList<>();

    public void interpretNote(int note) {

        int currentNodeSize = currentNodes.size();
        for (int i = 0; i < currentNodeSize; i++) {
            Node currentNode = currentNodes.get(i);
            if (currentNode.ruleMap.containsKey(note)) {
                currentNodes.add(currentNode.ruleMap.get(note));
                if (currentNode.ruleMap.get(note).value != 0) {
                    foundPattern(currentNode.ruleMap.get(note).value);
                } else {
                    //System.out.println("Next note found");
                }
            }
        }

        currentNodes.subList(0, currentNodeSize).clear();

        if (root.ruleMap.containsKey(note)) {
            System.out.println("Root pattern found");
            currentNodes.add(root.ruleMap.get(note));
            if (root.ruleMap.get(note).value != 0) {
                foundPattern(root.ruleMap.get(note).value);
            }
        }
    }

    public void printNoteSearch(Node root){
        for (int key : root.ruleMap.keySet()) {
            System.out.println("Key: " + key + " Value: " + root.ruleMap.get(key).value);
            printNoteSearch(root.ruleMap.get(key));
        }
    }

    private void initializePatterns(Pattern[] patterns) {
        for (Pattern pattern : patterns) {
            Node currentNode = root;
            for (int note : pattern.pattern) {
                if (currentNode.ruleMap.containsKey(note)) {
                    currentNode = currentNode.ruleMap.get(note);
                } else {
                    Node newNode = new Node();
                    currentNode.addRule(note, newNode);
                    currentNode = newNode;
                }
            }
            currentNode.setValue(pattern.value);
        }
    }

    private void foundPattern(byte value) {
        System.out.println("Pattern " + value + " found");
        if (value == 1) {
            showFreddy();
        } else if (value == 2) {
            showChristmas();
        }
    }

    private void showFreddy() {
        System.out.println("HarHarhar...");
        Popup freddyPopup = new Popup("/freddy.jpg", "Freddy");
        freddyPopup.showPopup();

        //HolderFrame freddyFrame = new HolderFrame("/freddy.jpg", "Freddy");

    }

    private void showChristmas() {
        System.out.println("Merry Christmas!");
        Popup christmasPopup = new Popup("/snoopyChristmas.gif", "Christmas");
        christmasPopup.showPopup();
    }
}

class Pattern {
    public int[] pattern;
    public byte value = 0;

    public Pattern(int[] pattern, byte value) {
        this.pattern = pattern;
        this.value = value;
    }

}
class Node {
    public HashMap<Integer, Node> ruleMap;
    public byte value = 0;
    public Node() {
        ruleMap = new HashMap<>();
    }

    public void addRule(int key, Node node) {
        ruleMap.put(key, node);
    }

    public void setValue(byte value) {
        this.value = value;
    }

}
