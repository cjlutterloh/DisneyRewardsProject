//Carson Lutterloh
//cjl150530

package Customer;

public class PreferredCustomer extends RegCustomer {
    //Variables
    double discountPercentage;
    
    //Constructors
    public PreferredCustomer() {
        super();
        discountPercentage = 0;
    }
    public PreferredCustomer(double discountPercentage) {
        super();
        this.discountPercentage = discountPercentage;
    }
    public PreferredCustomer(String firstName, String lastName, long guestID, double amountSpent, double discountPercentage) {
        super(firstName, lastName, guestID, amountSpent);
        this.discountPercentage = discountPercentage;
    }
    
    //Accessors
    @Override
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    @Override
    //Mutators
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    //Used for testing
    @Override
    public String toString()
    {
        return "Name: " + firstName +" " + lastName + " ID: " + guestID + " Amount Spent: " + String.format("%.2f", amountSpent) + " Discount: " + discountPercentage;
    }
}
