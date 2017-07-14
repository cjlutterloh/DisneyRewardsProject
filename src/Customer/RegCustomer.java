//Carson Lutterloh
//cjl150530

package Customer;

public class RegCustomer {
    
    //Variables
    String firstName;
    String lastName;
    long guestID;
    double amountSpent;
    
    //Constructors
    public RegCustomer() {
        this(" "," ",0 ,0);
    }
    public RegCustomer(String firstName, String lastName, long guestID, double amountSpent) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.guestID = guestID;
        this.amountSpent = amountSpent;
    }
    
    //Accessors
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public long getGuestID() {
        return guestID;
    }
    public double getAmountSpent() {
        return amountSpent;
    }
    
    //Mutators
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setGuestID(long guestID) {
        this.guestID = guestID;
    }
    public void setAmountSpent(double amountSpent) {
        this.amountSpent = amountSpent;
    }
    
    //Needed for inheritance purposes
    public double getDiscountPercentage() {
        System.out.println("Error! The customer object does not have a discount to get");
        return 0;
    }
    public void setDiscountPercentage(double discountPercentage) {
        System.out.println("Error! The customer object does not have a discount to set");
    }
    
    //Used for testing
    @Override
    public String toString()
    {
        return "Name: " + firstName +" " + lastName + " ID: " + guestID + " Amount Spent: " + String.format("%.2f", amountSpent);
    }
}
