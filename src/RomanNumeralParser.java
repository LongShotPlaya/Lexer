import java.util.ArrayList;
import java.util.List;

class TreeNode {
    String value;
    List<TreeNode> children;
    TreeNode parent;

    public TreeNode(String value, List<TreeNode> children) {
        this.value = value;
        this.children = children != null ? children : new ArrayList<>();
        this.parent = null;
    }
}

public class RomanNumeralParser {

    public static List<String> lexRomanNumerals(String romanNumeral) {
        List<String> tokens = new ArrayList<>();
        String remainingInput = romanNumeral;

        String[][] patterns = {
                { "M", "1000" },
                { "CM", "900" },
                { "D", "500" },
                { "CD", "400" },
                { "C", "100" },
                { "XC", "90" },
                { "L", "50" },
                { "XL", "40" },
                { "X", "10" },
                { "IX", "9" },
                { "V", "5" },
                { "IV", "4" },
                { "I", "1" }
        };

        // Cant repeat at all
        int VCount = 0;
        int LCount = 0;
        int DCount = 0;

        // Cant repeat more than 3 times
        int ICount = 0;
        int XCount = 0;
        int CCount = 0;
        int MCount = 0;

        // for (String[] pattern : patterns){
        //     while(!remainingInput.isEmpty()){
        //         //check if current character is the same as the last character
        //         if(remainingInput.startsWith(pattern[0])){
        //             tokens.add(pattern[1]);
        //             remainingInput = remainingInput.substring(pattern[0].length());
        //             if(!remainingInput.startsWith(pattern[0]))
        //                 break;
        //         }
        //     }
        // }
        // if(!remainingInput.isEmpty()){
        //     throw new IllegalArgumentException("Invalid Roman numeral: " + romanNumeral);
        // }

        while (!remainingInput.isEmpty()) {
            boolean matchFound = false;
            for (String[] pattern : patterns) {
                if (remainingInput.startsWith(pattern[0])) {
                    String token = pattern[1];
                    
                    // V, L and D
                    if (token.equals("5")) {
                        if (VCount > 0) {
                            throw new IllegalArgumentException("The Character V cannot repeat: " + romanNumeral);
                        }
                        VCount++;
                        LCount = 0;
                        DCount = 0;
                    } else if (token.equals("50")) {
                        if (LCount > 0) {
                            throw new IllegalArgumentException("The Character L cannot repeat: " + romanNumeral);
                        }
                        VCount = 0;
                        LCount++;
                        DCount = 0;
                    } else if (token.equals("500")) {
                        if (DCount > 0) {
                            throw new IllegalArgumentException("The Character D cannot repeat:" + romanNumeral);
                        }
                        VCount = 0;
                        LCount = 0;
                        DCount++;
                    } else { // I, X, C and M
                        if (token.equals("1")) {
                            ICount++;
                            XCount = 0;
                            CCount = 0;
                            MCount = 0;
                        } else if (token.equals("10")) {
                            ICount = 0;
                            XCount++;
                            CCount = 0;
                            MCount = 0;
                        } else if (token.equals("100")) {
                            ICount = 0;
                            XCount = 0;
                            CCount++;
                            MCount = 0;
                        } else if (token.equals("1000")) {
                            ICount = 0;
                            XCount = 0;
                            CCount = 0;
                            MCount++;
                        }
                        
                        // Validation
                        if (ICount > 3 || XCount > 3 || CCount > 3 || MCount > 3) {
                            throw new IllegalArgumentException("No character can repeate itself more than 3 times: " + romanNumeral);
                        }
                    }
                    
                    tokens.add(token);
                    remainingInput = remainingInput.substring(pattern[0].length());
                    matchFound = true;
                    break;
                }
            }

            if (!matchFound) {
                throw new IllegalArgumentException("Invalid Roman numeral: " + romanNumeral);
            }
        }

        return tokens;
    }

    public static TreeNode parseRomanNumerals(List<String> tokens) {
        if (tokens.isEmpty()) {
            return null;
        }

        TreeNode root = new TreeNode(null, new ArrayList<>());
        TreeNode currentNode = root;

        for (String token : tokens) {
            while (currentNode.value != null && Integer.parseInt(currentNode.value) <= Integer.parseInt(token)) {
                currentNode = currentNode.parent;
            }

            TreeNode newNode = new TreeNode(token, new ArrayList<>());
            currentNode.children.add(newNode);
            newNode.parent = currentNode;
            currentNode = newNode;
        }

        return root;
    }

    public static void printParseTree(TreeNode node, int depth) {
        if (node != null) {
            System.out.println("  ".repeat(depth) + node.value);
            for (TreeNode child : node.children) {
                printParseTree(child, depth + 1);
            }
        }
    }

    public static int computeRomanValue(TreeNode node) {
        if (node == null) {
            return 0;
        }

        int value = node.value != null ? Integer.parseInt(node.value) : 0;
        for (TreeNode child : node.children) {
            value += computeRomanValue(child);
        }

        return value;
    }

    public static boolean hasError(String romanNumeral) {
        try {
            lexRomanNumerals(romanNumeral);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    public static void parse(String romanNumeral) {
        if (hasError(romanNumeral)) {
            System.out.println("'" + romanNumeral + "' has error");
            return;
        }

        System.out.println("Roman Numeral: " + romanNumeral);

        List<String> tokens = lexRomanNumerals(romanNumeral);
        System.out.println("Tokens: " + tokens);

        TreeNode parseTree = parseRomanNumerals(tokens);
        System.out.println("Parse Tree:");
        printParseTree(parseTree, 0);

        int result = computeRomanValue(parseTree);
        System.out.println("Computed Value: " + result);
    }

    public static void main(String[] args) {
        parse("XXXVIII");
        parse("MCMDCDCXCLXLXIXVIVI");
        parse("VVL");
        parse("XXXX");
        parse("MMMM");
        parse("abc"); // non-Roman Numerals
    }
}
