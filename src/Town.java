/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean treasureCollected;
    private boolean hasDug;



    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        hasDug=false;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() { return printMessage; }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
        assignTreasure();
    }
    public void assignTreasure(){
        double rnd = (int) ((Math.random() * 4)+1);
        if(rnd==1){
            treasure="crown";
        } else if (rnd==2) {
            treasure="trophy";
        } else if(rnd==3){
            treasure="gem";
        }else {
            treasure="dust";
        }
        treasureCollected=false;
    }
    public void searchTreasure(){

        if(!treasureCollected) {
            int idx = -1;
            for (int i = 0; i < hunter.getTreasures().length; i++) {
                if (hunter.getTreasures()[i] == null) {
                    idx = i;
                    break;
                }
            }
            if(idx==-1){
                System.out.println("You found a "+treasure);
                System.out.println("Your treasure inventory is full. No more treasures can be collected");
            } else if(idx>=0) {
                int count = 0;
                for (int i = 0; i < hunter.getTreasures().length; i++) {
                    if (hunter.getTreasures()[i] != null && hunter.getTreasures()[i].equals(treasure)) {
                        count++;
                    }
                }
                if (count == 0) {
                    System.out.println("You found a "+treasure);
                    if(!treasure.equals("dust")){
                        hunter.getTreasures()[idx] = treasure;
                    }
                }else{
                    System.out.println("You found a "+treasure);
                    System.out.println("You already have this treasure!");
                }
                setTreasureCollected(true);
            }
        } else{
            System.out.println("You already searched this town!");
        }


    }
    public void dig(){
        if(!hasDug) {
            if (hunter.hasItemInKit("shovel")) {
                if (Math.random() > .5) {
                    int goldFound = ((int) (Math.random() * 20)) + 1;
                    hunter.changeGold(goldFound);
                    System.out.println("You dug up " + goldFound + " gold!");
                    hasDug = true;
                } else {
                    System.out.println("You dug but only found dirt");
                    hasDug = true;
                }
            } else {
                System.out.println("You can't dig for gold without a shovel");
            }

        }else{
            System.out.println("You already dug for gold in this town.");
        }
    }


    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + Colors.CYAN +terrain.getTerrainName()+ Colors.RESET + ".";
            if (!TreasureHunter.easyMode && checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item + ".";
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        double loseChance;
       if (TreasureHunter.easyMode) {
            if (toughTown) {
                noTroubleChance = 0.66;
                loseChance = .33;
            } else {
                noTroubleChance = 0.33;
                loseChance = .15;
            }
            if (Math.random() > noTroubleChance) {
                printMessage = "You couldn't find any trouble";
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > loseChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                    printMessage += "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(-goldDiff);

                }
            }
        } else if (Hunter.hasSword) {
            loseChance = -1;
            if (toughTown) {
                noTroubleChance = 0.66;
            } else {
                noTroubleChance = 0.33;
            }
            if (Math.random() > noTroubleChance) {
                printMessage = "You couldn't find any trouble";
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > loseChance) {
                    printMessage += "The brawler, seeing your sword, realizes he picked a losing fight and gives you his gold." + Colors.RESET;
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                    printMessage += "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(-goldDiff);

                }
            }
        } else {
            if (toughTown) {
                noTroubleChance = 0.66;
            } else {
                noTroubleChance = 0.33;
            }
            if (Math.random() > noTroubleChance) {
                printMessage = "You couldn't find any trouble";
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                    printMessage += "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(-goldDiff);

                }
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + Colors.CYAN+terrain.getTerrainName()+Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .16666) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .33333) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .66666) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .83333) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

    public String getPrintMessage() {
        return printMessage;
    }
    public void setTreasureCollected(boolean treasureCollected) {
        this.treasureCollected = treasureCollected;
    }


    public boolean isTreasureCollected() {
        return treasureCollected;
    }

    public boolean isThreeTreasures() {
        int count=0;
        for (int i = 0; i < hunter.getTreasures().length; i++) {
            if(hunter.getTreasures()[i]!=null){
                count++;
            }
        }
        if(count==3){
            return true;
        }else{
            return false;
        }
    }
}