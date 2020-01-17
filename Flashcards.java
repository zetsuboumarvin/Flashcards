import java.io.*;
import java.util.*;

public class Flashcards {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        HashMap<String, Integer> difficulty = new HashMap<>();
        LinkedList<String> log = new LinkedList<>();
        boolean imp = false;
        int impInd = 0;
        int expInd = 0;
        boolean exp = false;

        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-import")) {
                    imp = true;
                    impInd = i + 1;
                }
                if (args[i].equals("-export")) {
                    exp = true;
                    expInd = i + 1;
                }
            }
            if ((!imp && !exp)) {
                System.out.println("Usage: java Flashcards [-import filename] [-export filename]");
                return;
            }
            if (imp) {
                if (impInd < args.length)
                    importCardFromArg(map, log, difficulty, args[impInd]);
                else {
                    System.out.print("Invalid import option. ");
                    System.out.println("Usage: java Flashcards [-import filename] [-export filename]");
                }
            }
            if (exp) {
                if (expInd >= args.length) {
                    System.out.print("Invalid export option. ");
                    System.out.println("Usage: java Flashcards [-import filename] [-export filename]");
                }
            }
        }

        while (true) {
            System.out.print("Input the action ");
            System.out.println("(add, remove, import, export, ask, exit, help, log, hardest card, reset stats):");
            log.add("Input the action ");
            log.add("(add, remove, import, export, ask, exit, help, log, hardest card, reset stats):\n");
            String action = sc.nextLine().toLowerCase();
            log.add(action + "\n");
            switch (action) {
                case "add":
                    getCard(map, log);
                    break;
                case "remove":
                    removeCard(map, log, difficulty);
                    break;
                case "import":
                    importCard(map, log, difficulty);
                    break;
                case "export":
                    exportCard(map, log, difficulty);
                    break;
                case "ask":
                    askCards(map, log, difficulty);
                    break;
                case "help":
                    System.out.println("add - create a new card (you should input term and definition)");
                    log.add("add - create a new card (you should input term and definition)\n");
                    System.out.println("remove - delete a card (you should input term of the card)");
                    log.add("remove - delete a card (you should input term of the card)\n");
                    System.out.print("import - insert cards from a file (file should contain pair of lines - ");
                    System.out.println("term and definition of the card, and the third line - difficulty");
                    log.add("import - insert cards from a file (file should contain pair of lines - ");
                    log.add("term and definition of the card, and the third line - difficulty\n");
                    System.out.println("export - save cards in a file");
                    log.add("export - save cards in a file\n");
                    System.out.println("ask - program asks definitions of random displayed terms");
                    log.add("ask - program asks definitions of random displayed terms\n");
                    System.out.println("exit - close the program");
                    log.add("exit - close the program\n");
                    System.out.println("log - saves all actions of current session in the file");
                    log.add("log - saves all actions of current session in the file\n");
                    System.out.println("hardest card - display the card which was answered wrong more often");
                    log.add("hardest card - display the card which was answered wrong more often\n");
                    System.out.println("reset stats - delete statistics of mistakes\n");
                    log.add("reset stats - delete statistics of mistakes\n\n");
                    break;
                case "exit":
                    break;
                case "log":
                    saveLog(log);
                    break;
                case "hardest card":
                    getHardest(log, difficulty);
                    break;
                case "reset stats":
                    difficulty.clear();
                    System.out.println("Card statistics has been reset.\n");
                    log.add("Card statistics has been reset.\n\n");
                    break;
                default:
                    System.out.println("Unknown action.\n");
                    log.add("Unknown action.\n\n");
                    break;
            }
            if (action.equals("exit")) {
                System.out.println("Bye bye!");
                if (exp)
                    exportCardFromArg(map, log, difficulty, args[expInd]);
                break;
            }
        }
    }

    private static void getCard(LinkedHashMap<String, String> map, LinkedList<String> log) {
        Scanner sc = new Scanner(System.in);

        System.out.println("The card:");
        log.add("The card:\n");
        String key = sc.nextLine();
        log.add(key + "\n");
        if (map.containsKey(key)) {
            System.out.println("The card \"" + key + "\" already exists.\n");
            log.add("The card \"" + key + "\" already exists.\n\n");
            return;
        }
        System.out.println("The definition of the card:");
        log.add("The definition of the card:\n");
        String value = sc.nextLine();
        log.add(value + "\n");
        if (map.containsValue(value)) {
            System.out.println("The definition \"" + value + "\" already exists.\n");
            log.add("The definition \"" + value + "\" already exists.\n\n");
            return;
        }
        map.put(key, value);
        System.out.println("The pair (\"" + key + "\":\"" + value + "\") has been added.\n");
        log.add("The pair (\"" + key + "\":\"" + value + "\") has been added.\n\n");
    }

    private static void removeCard(LinkedHashMap<String, String> map, LinkedList<String> log,
                                   HashMap<String, Integer> difficulty) {
        Scanner sc = new Scanner(System.in);

        System.out.println("The card:");
        log.add("The card:\n");
        String key = sc.nextLine();
        log.add(key + "\n");
        if (!map.containsKey(key)) {
            System.out.println("Can't remove \"" + key + "\": there is no such card.\n");
            log.add("Can't remove \"" + key + "\": there is no such card.\n\n");
        } else {
            map.remove(key);
            System.out.println("The card has been removed.\n");
            log.add("The card has been removed.\n\n");
            difficulty.remove(key);
        }
    }

    private static void importCard(LinkedHashMap<String, String> map, LinkedList<String> log,
                                   HashMap<String, Integer> difficulty) {
        Scanner sc = new Scanner(System.in);

        System.out.println("File name:");
        log.add("File name:\n");
        File file = new File(sc.nextLine());
        log.add(file.getName() + "\n");
        try (Scanner fsc = new Scanner(file)) {
            int count = 0;
            boolean invFile = false;
            while (fsc.hasNextLine()) {
                String key = fsc.nextLine();
                log.add(key + "\n");
                if (!fsc.hasNextLine()) {
                    System.out.println("Invalid file. " + count + " cards have been loaded.\n");
                    log.add("Invalid file. " + count + " cards have been loaded.\n\n");
                    invFile = true;
                    break;
                }
                String value = fsc.nextLine();
                log.add(value + "\n");
                map.put(key, value);
                if (!fsc.hasNextLine()) {
                    System.out.println("Invalid file. " + count + " cards have been loaded.\n");
                    log.add("Invalid file. " + count + " cards have been loaded.\n\n");
                    invFile = true;
                    break;
                }
                int diff = Integer.parseInt(fsc.nextLine());
                log.add(diff + "\n");
                if (diff != 0)
                    difficulty.put(key, diff);
                count++;
            }
            if (!invFile) {
                System.out.println(count + " cards have been loaded.\n");
                log.add(count + " cards have been loaded.\n\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.\n");
            log.add("File not found.\n\n");
        }
    }

    private static void exportCard(LinkedHashMap<String, String> map, LinkedList<String> log,
                                   HashMap<String, Integer> difficulty) {
        Scanner sc = new Scanner(System.in);

        System.out.println("File name:");
        log.add("File name:\n");
        String fileName = sc.nextLine();
        log.add(fileName + "\n");
        if (fileName.length() == 0) {
            System.out.println("Empty line.\n");
            log.add("Empty line.\n\n");
            return;
        }
        File file = new File(fileName);
        try (PrintWriter fw = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                fw.println(entry.getKey());
                fw.println(entry.getValue());
                log.add(entry.getKey() + "\n");
                log.add(entry.getValue() + "\n");
                if (difficulty.containsKey(entry.getKey()))
                    fw.println(difficulty.get(entry.getKey()));
                else
                    fw.println(0);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            log.add(e.getMessage() + "\n");
        }
        System.out.println(map.size() + " cards have been saved.\n");
        log.add(map.size() + " cards have been saved.\n\n");
    }

    private static void askCards(LinkedHashMap<String, String> map, LinkedList<String> log,
                                 HashMap<String, Integer> difficulty) {
        Scanner sc = new Scanner(System.in);
        int count = 0;

        System.out.println("How many times to ask?");
        log.add("How many times to ask?\n");
        try {
            count = sc.nextInt();
            log.add(Integer.toString(count) + "\n");
        } catch (InputMismatchException e) {
            System.out.println("Invalid number.");
            log.add("Invalid number.\n");
            return;
        }
        while (count-- > 0) {
            Map.Entry<String, String> e = getRandomEntry(map);
            if (e != null) {
                if (!checkAnswer(map, e, log)) {
                    if (difficulty.containsKey(e.getKey()))
                        difficulty.put(e.getKey(), difficulty.get(e.getKey()) + 1);
                    else
                        difficulty.put(e.getKey(), 1);
                }
            }
            else {
                System.out.println("Unexpected error :)\n");
                log.add("Unexpected error :)\n\n");
                break;
            }
        }
    }

    private static Map.Entry<String, String> getRandomEntry(LinkedHashMap<String, String> map) {
        Random random = new Random();

        int seed = random.nextInt(map.size());
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (seed == 0)
                return e;
            seed--;
        }
        return null;
    }

    private static boolean checkAnswer(LinkedHashMap<String, String> map,
                                    Map.Entry<String, String> e, LinkedList<String> log) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Print the definition of \"" + e.getKey() + "\":");
        log.add("Print the definition of \"" + e.getKey() + "\":\n");
        String s = sc.nextLine();
        log.add(s + "\n");
        if (!s.equals(e.getValue()) && map.containsValue(s)) {
            String rightKey = "";
            for (Map.Entry<String, String> t : map.entrySet()) {
                if (s.equals(t.getValue())) {
                    rightKey = t.getKey();
                    break;
                }
            }
            System.out.print("Wrong answer. The correct one is \"" + e.getValue() + "\", you've just ");
            System.out.println("written the definition of \"" + rightKey + "\".\n");
            log.add("Wrong answer. The correct one is \"" + e.getValue() + "\", you've just ");
            log.add("written the definition of \"" + rightKey + "\".\n\n");
            return false;
        } else if (!s.equals(e.getValue())) {
            System.out.println("Wrong answer. The correct one is \"" + e.getValue() + "\".\n");
            log.add("Wrong answer. The correct one is \"" + e.getValue() + "\".\n\n");
            return false;
        } else {
            System.out.println("Correct answer.\n");
            log.add("Correct answer.\n\n");
            return true;
        }
    }

    private static void saveLog(LinkedList<String> log) {
        Scanner sc = new Scanner(System.in);
        LinkedList<String> tempLog = null;

        System.out.println("File name:");
        log.add("File name:\n");
        String fileName = sc.nextLine();
        log.add(fileName + "\n");
        if (fileName.length() == 0) {
            System.out.println("Empty line.\n");
            log.add("Empty line.\n\n");
            return;
        }
        File file = new File(fileName);
        try (PrintWriter fw = new PrintWriter(new FileWriter(file))) {
            tempLog = new LinkedList<>(log);
            for (String s : log) {
                fw.print(s);
                tempLog.add(s);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        log = tempLog;
        System.out.println("The log has been saved.\n");
        log.add("The log has been saved.\n\n");
    }

    private static void getHardest(LinkedList<String> log, HashMap<String, Integer> difficulty) {
        int max = 0;
        LinkedList<String> hardestKey = new LinkedList<>();

        for (Map.Entry<String, Integer> e : difficulty.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                hardestKey.clear();
                hardestKey.add(e.getKey());
            } else if (e.getValue() == max) {
                hardestKey.add(e.getKey());
            }
        }
        if (hardestKey.size() > 0) {
            System.out.print("The hardest card");
            log.add("The hardest card");
            if (hardestKey.size() > 1) {
                System.out.print("s are ");
                log.add("s");
            } else {
                System.out.print(" is ");
                log.add(" is ");
            }
            int i = 0;
            for (; i < hardestKey.size() - 1; i++) {
                System.out.print("\"" + hardestKey.get(i) + "\", ");
                log.add("\"" + hardestKey.get(i) + "\", ");
            }
            System.out.print("\"" + hardestKey.get(i) + "\". ");
            log.add("\"" + hardestKey.get(i) + "\". ");
            if (difficulty.get(hardestKey.get(0)) > 1) {
                System.out.print("You have " + difficulty.get(hardestKey.get(0)) + " errors answering ");
                log.add("You have " + difficulty.get(hardestKey.get(0)) + " errors answering ");
            } else {
                System.out.print("You have 1 error answering ");
                log.add("You have 1 error answering ");
            }
            if (hardestKey.size() > 1) {
                System.out.println("them.\n");
                log.add("them.\n\n");
            } else {
                System.out.println("it.\n");
                log.add("it.\n\n");
            }
        } else {
            System.out.println("There are no cards with errors.\n");
            log.add("There are no cards with errors.\n\n");
        }
    }

    private static void importCardFromArg(LinkedHashMap<String, String> map, LinkedList<String> log,
                                   HashMap<String, Integer> difficulty, String fileName) {
        File file = new File(fileName);
        try (Scanner fsc = new Scanner(file)) {
            int count = 0;
            boolean invFile = false;
            while (fsc.hasNextLine()) {
                String key = fsc.nextLine();
                log.add(key + "\n");
                if (!fsc.hasNextLine()) {
                    System.out.println("Invalid file. " + count + " cards have been loaded.\n");
                    log.add("Invalid file. " + count + " cards have been loaded.\n\n");
                    invFile = true;
                    break;
                }
                String value = fsc.nextLine();
                log.add(value + "\n");
                map.put(key, value);
                if (!fsc.hasNextLine()) {
                    System.out.println("Invalid file. " + count + " cards have been loaded.\n");
                    log.add("Invalid file. " + count + " cards have been loaded.\n\n");
                    invFile = true;
                    break;
                }
                int diff = Integer.parseInt(fsc.nextLine());
                log.add(diff + "\n");
                if (diff != 0)
                    difficulty.put(key, diff);
                count++;
            }
            if (!invFile) {
                System.out.println(count + " cards have been loaded.\n");
                log.add(count + " cards have been loaded.\n\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.\n");
            log.add("File not found.\n\n");
        }
    }

    private static void exportCardFromArg(LinkedHashMap<String, String> map, LinkedList<String> log,
                                   HashMap<String, Integer> difficulty, String fileName) {
        if (fileName.length() == 0) {
            System.out.println("Empty line.\n");
            log.add("Empty line.\n\n");
            return;
        }
        File file = new File(fileName);
        try (PrintWriter fw = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                fw.println(entry.getKey());
                fw.println(entry.getValue());
                log.add(entry.getKey() + "\n");
                log.add(entry.getValue() + "\n");
                if (difficulty.containsKey(entry.getKey()))
                    fw.println(difficulty.get(entry.getKey()));
                else
                    fw.println(0);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            log.add(e.getMessage() + "\n");
        }
        System.out.println(map.size() + " cards have been saved.\n");
        log.add(map.size() + " cards have been saved.\n\n");
    }

}
