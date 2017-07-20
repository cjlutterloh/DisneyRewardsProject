//cjlutterloh
//CS 2336

/*
Project Notes:
1. Here at Disney, we are nice. This means that if a customer spends $149.995, it rounds up to $150. We are willing to give them half a cent for free.
It would be quite easy to subtract .005 to aid in rounding down, but we decided to be generous.
*/

import Customer.*;
import java.io.*;
import java.util.*; //For Scanner

public class Main {
    public static void main(String[] args) throws IOException {
        //Open files and check that they're valid
        File customerInfo = new File("customer.dat");
        File preferredCustomerInfo = new File("preferred.dat");
        File ordersInfo = new File("orders.dat");
        checkFile(customerInfo);
        //The preferred.dat file MAY not exist, so this ensures that it does, and still checks the file in case something slips by
        if (preferredCustomerInfo.createNewFile()){
	        //TESTtext System.out.println("File is created!");
        }
        else {
            checkFile(preferredCustomerInfo);
        }
        checkFile(ordersInfo);
        
        //Initialize customer array
        int numberOfCustomers = readFileLines(customerInfo);
        RegCustomer[] customerArray = new RegCustomer[numberOfCustomers];
        //The array has to be created before this for location purposes, but it is not given any values if there are no customers
        if (numberOfCustomers > 0) {
            //One method write regular customers and preferred, so the boolean makes sure it is process correctly
            boolean preferred = false;
            customerArray = writeDataToArray(customerInfo, customerArray, preferred);
        }
        else {
            System.out.println("You do not currently have any customers");
        }
        
        //Initialize preferred customer array
        int numberOfPreferredCustomers = readFileLines(preferredCustomerInfo);
        RegCustomer[] preferredCustomerArray = new PreferredCustomer[numberOfPreferredCustomers];
        if (numberOfPreferredCustomers > 0) {
            boolean preferred = true;
            preferredCustomerArray = writeDataToArray(preferredCustomerInfo, preferredCustomerArray, preferred);
        }
        else {
            System.out.println("You do not currently have any preferred customers");
        }
        
        //Process Orders
        processOrders(ordersInfo, customerArray, preferredCustomerArray, customerInfo, preferredCustomerInfo);
    }
    
    public static void checkFile (File filename) {
        //If a necessary file does not exist, the program terminates
        if (!filename.exists()) {
                System.out.println("Error! The file " + filename + " does not exist in the directory.");
                System.exit(-1);
            }
    }
    
    public static int readFileLines(File fileName) throws IOException {
        //Determine the number of rows in the file (We need this to create an array of the right size!)
        String line;
        int count;
        try ( //Reads file line by line
            Scanner lineReader = new Scanner(fileName)
        ) {
            //Determines the number of rows
            count = 0;
            while (lineReader.hasNext()) {
                line = lineReader.nextLine();
                if (line.length() > 0) {    //This ensures that an empty line is not looked at
                    count++;
                }
            }
        }
        return count;
    }
    
    public static RegCustomer[] writeDataToArray(File fileName, RegCustomer[] array, boolean specialCase) throws IOException {
        try (
            Scanner writer = new Scanner(fileName);
        )  {
            //Scan the files as long as there's something to scan. Takes user info to write to arrays
            while (writer.hasNext()) {
                for (int i = 0; i < array.length; i++) {
                    //Rather than make two methods for Preferred and Regular, we made one that takes a boolean to make the small changes needed
                    if (specialCase) {
                        array[i] = new PreferredCustomer();
                    }
                    else {
                        array[i] = new RegCustomer();
                    }
                    array[i].setGuestID(writer.nextLong());
                    array[i].setFirstName(writer.next());
                    array[i].setLastName(writer.next());
                    array[i].setAmountSpent(writer.nextDouble());
                    if (specialCase) {
                        //This reads in a string (Ex. 5%) and converts it to a double (Ex. 5)
                        String percentDiscount = writer.next();
                        (array[i]).setDiscountPercentage(Double.parseDouble(percentDiscount.replace("%",""))); //This works because of an overridden method! It also replaces the % sign automatically
                    }
                }
            }
        }
        catch (InputMismatchException e) {  //If the file doesn't have a double or int where it should, this error will be triggered and quit the program
            if (specialCase)
                System.out.println("The preferred customer file is not formatted correctly. Exiting...");
            else
                System.out.println("The customer file is not formatted correctly. Exiting...");
            System.exit(-1);
        }
        return array;
    }
    
    public static void processOrders(File fileName, RegCustomer[] array, RegCustomer[] specialArray, File fileToWrite, File specialFileToWrite) throws IOException {
        int orderNumber = 0; //Will be used for convenience in reporting a problem (Ex. The user ID for order #2 did not match anything)
        try (
            Scanner orderProcessor = new Scanner(fileName);
        )  {
            //Scan the files as long as necessary
            //Process each order 1 by 1
            while (orderProcessor.hasNext()) {  //To improve performance SLIGHTLY, these could be declared outside the loop. The compiler can handle it very easily though, so this greatly benefits readability
                //Hold all values for each order
                int placeholder = -1;
                int orderID = orderProcessor.nextInt();
                double containerRadius = orderProcessor.nextDouble();
                double containerHeight = orderProcessor.nextDouble();
                double ounces = orderProcessor.nextDouble();
                double ouncePrice = orderProcessor.nextDouble();
                double squareInchPrice = orderProcessor.nextDouble();
                int quantity = orderProcessor.nextInt();
                //Special will be needed later on to use the discount in calculations
                boolean special = false;
                //This variable is being declared for customers who get an updated discount
                double newDiscount;
                
                //Find a matching ID. This checks all regular customers until it finds one
                for (int i = 0; i < array.length; i++) {
                    //If the ID matches and there hasn't already been a match. This keeps this from executing too many times
                    if (orderID == array[i].getGuestID() && placeholder == -1) {    
                        //TESTtext System.out.println("This order is for " + array[i].toString());
                        placeholder = i;
                    }
                }
                //This checks all preferred customers for a matching ID until it finds one
                for (int j = 0; j < specialArray.length; j++) {
                    if (orderID == specialArray[j].getGuestID() && placeholder == -1) {
                        //TESTtext System.out.println("This order is for " + specialArray[j].toString());
                        placeholder = j;
                        special = true;
                    }
                    //This only executes if no matching ID has been found and it's the last ID to check
                    else if (placeholder == -1 && j == specialArray.length - 1){    
                        System.out.println("No matching user ID for order #" + orderNumber);
                    }
                }
                orderNumber++;
                
                //Update customer
                if (placeholder > -1) {
                    //Calculate the price by ounces
                    double priceOfDrink = ounces * ouncePrice;
                    //Add the price of any personalization, if it exists
                    if (squareInchPrice > 0) {
                        priceOfDrink += (((2 * Math.PI * containerRadius * containerHeight) + (2 * Math.PI * Math.pow(containerRadius, 2))) * squareInchPrice);
                    }
                    //Multiply price by quantity
                    priceOfDrink *= quantity;
                    //Apply discount if it exists, and update the customer's amount spent
                    if (special) {
                        //TESTtext System.out.println("PREFFERED CUSTOMER");
                        priceOfDrink *= (1 - (specialArray[placeholder].getDiscountPercentage() / 100));
                        specialArray[placeholder].setAmountSpent(specialArray[placeholder].getAmountSpent() + priceOfDrink);
                    }
                    else {
                        array[placeholder].setAmountSpent(array[placeholder].getAmountSpent() + priceOfDrink);
                    }
                    //TESTtext System.out.println(priceOfDrink);
                    
                    //Upgrade customer
                    //If a basic customer goes over $150
                    if (!special && (array[placeholder].getAmountSpent() >= 149.995)) { //Gives customers the benefit of the doubt on $149.995. It's necessary for accuracy too
                        newDiscount = 5;
                        specialArray = addUpgradeStatus(array, specialArray, placeholder, newDiscount); //Adds customer to the preferred array
                        array = deleteUpgradeStatus(array, placeholder);   //Deletes customer from regular array
                        special = true; //This is important in case they are new preferred customers AND spend over $250 too
                        placeholder = specialArray.length - 1;  //The old placeholder corresponded to the regular array. We updated it to correspond to the new array
                    }
                    
                    //If a customer at a 5% discount gets over the other thresholds... (Includes customers just upgraded)
                    if (special && specialArray[placeholder].getDiscountPercentage() == 5.0) {
                        if (specialArray[placeholder].getAmountSpent() >= 349.995) {
                            specialArray[placeholder].setDiscountPercentage(10.0);
                        }
                        else if (specialArray[placeholder].getAmountSpent() >= 199.995) {
                            specialArray[placeholder].setDiscountPercentage(7.0);
                        }
                    }
                    //If a customer at a 7% discount gets over the $350 threshold (Checks one thing instead of 2. Minorly saves time)
                    else if (special && specialArray[placeholder].getDiscountPercentage() == 7.0) {
                        if (specialArray[placeholder].getAmountSpent() >= 349.995) {
                            specialArray[placeholder].setDiscountPercentage(10.0);
                        }
                    }
                }
            }
            //Write new results to file
            writeDataToFile (array, specialArray, fileToWrite, specialFileToWrite);
        }
        catch (InputMismatchException e) {  //If the file doesn't have a double or int where it should, this error will be triggered and quit the program
            System.out.println("The orders file is not formatted correctly. Exiting");
            System.exit(-1);
        }
    }
    
    //This array must return the array or the processOrder method won't know the size has changed
    public static RegCustomer[] addUpgradeStatus (RegCustomer[] array, RegCustomer[] specialArray, int number, double discount) {
        //Update preferred customer array by putting everything in a tempArray (including the new preferred customer), adding 1 space to the old array, then making them equal
        RegCustomer[] tempArray = new PreferredCustomer[specialArray.length + 1];
        for (int i = 0; i < specialArray.length; i++) {
            tempArray[i] = specialArray[i];
        }
        //Initializes the new preferred customer at the end of the array
        tempArray[specialArray.length] = new PreferredCustomer(array[number].getFirstName(), array[number].getLastName(), array[number].getGuestID(), array[number].getAmountSpent(), discount);
        //Makes the old array equal to an array with 1 more space
        specialArray = tempArray;
        
        /*TESTING
        System.out.println("New list of preferred customers! (I am on line 182)");
        for (int i = 0; i < specialArray.length + 1; i++) {
            System.out.println(tempArray[i].toString());
        }
        System.out.println("End of new preferred customers! (I am on line 186)");*/
    
        return specialArray;
    }
    
    //This array must return the array or the processOrder method won't know the size has changed
    public static RegCustomer[] deleteUpgradeStatus (RegCustomer[] array, int number) {
        //Update regular customer array
        RegCustomer[] tempArray = new RegCustomer[array.length - 1];
        //Moves everything after the replaced customer down 1 space in the array
        if (number < array.length - 1) {
            for (int i = number; i < array.length - 1; i++) {
                array[i] = array[i + 1];
            }
        }
        //Writes everything except for the final space of the array (Which is now empty) to a temporary array
        for (int i = 0; i < array.length - 1; i++) {
            tempArray[i] = array[i];
        }
        //Makes the old array equal to an array with 1 less space
        array = tempArray;
        
        /* TESTING
        System.out.println("New list of customers! (I am on line 182)");
        for (int i = 0; i < array.length - 1; i++) {
            System.out.println(tempArray[i].toString());
        }
        System.out.println("End of new customers! (I am on line 186)");*/
        
        return array;
    }
    
    public static void writeDataToFile (RegCustomer[] array, RegCustomer[] specialArray, File fileToWrite, File specialFileToWrite) throws IOException {
        //Writes all necessary info to customer.dat
        try (
            PrintWriter output = new PrintWriter(fileToWrite);
        )   {
            for (int i = 0; i < array.length; i++) {
                output.println(array[i].getGuestID() + " " + array[i].getFirstName() + " " + array[i].getLastName() + " " + String.format("%.2f", array[i].getAmountSpent()));
            }
        }
        //Writes all necessary info to preferred.dat
        try (
            PrintWriter output = new PrintWriter(specialFileToWrite);
        )   {
            for (int i = 0; i < specialArray.length; i++) {
                output.println(specialArray[i].getGuestID() + " " + specialArray[i].getFirstName() + " " + specialArray[i].getLastName() + " " + String.format("%.2f", specialArray[i].getAmountSpent()) +  " " + String.format("%.0f", specialArray[i].getDiscountPercentage()) + "%");
            }
        }
    }
}
