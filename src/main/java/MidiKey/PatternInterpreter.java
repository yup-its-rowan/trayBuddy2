package MidiKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


    private Pattern testPattern = new Pattern(new int[]{69, 71, 72}, (byte) 1);
    private Pattern testPattern2 = new Pattern(new int[]{84, 83, 82, 81, 80}, (byte) 2);
    private Pattern[] patterns = {testPattern, testPattern2};

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
        for (int i = 0; i < currentNodeSize; i++) {
            currentNodes.remove(i);
        }
        if (root.ruleMap.containsKey(note)) {
            //System.out.println("Root pattern found");
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
        }
    }

    private void showFreddy() {
        System.out.println("HarHarhar...");
        FreddyPopup freddyPopup = new FreddyPopup();
        freddyPopup.showPopup();
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file) {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[2048];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
