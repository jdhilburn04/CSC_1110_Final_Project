
/**
 * Write a description of class Item here.
 *
 * @author Dylan Hilburn
 * @version 04-13-2024
 */
public class Item
{
    private String description;
    private double weight;
    private boolean isEdible;
    private double weightLimitModifier;
    
    public Item(String d) {
        description = d;
        weight = 0.5;
        isEdible = false;
        weightLimitModifier = 0;
    }
    
    public Item(String d, double w, boolean isEdible) {
        description = d;
        weight = w;
        this.isEdible = isEdible;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public String getLongDescription() {
        return description + " (weight: " + weight + "lbs)";
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setWeightLimitModifier(double v) {
        weightLimitModifier = v;
    }
    
    public double getWeightLimitModifier() {
        return weightLimitModifier;
    }
    
    public boolean getIsEdible() {
        return isEdible;
    }
    
}
