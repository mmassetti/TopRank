package tdp.miranking;

/**
 * Class used to define the specification of an item (entity)
 *
 * @author Matias Massetti
 * @version Jan-2017
 */

public class MyEntity {

    /**
     * Local variables
     */

    private int id;
    private String name;
    private float valuation;

    /**
     * Creates a new entity composed by a name and a valuation
     */

    public MyEntity(String n, float v) {
        name = n;
        valuation = v;
    }

    /**
     * Gets Id of the entity
     */
    public int getId() {
        return id;
    }

    /**
     * Sets Id of the entity
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets Name of the entity
     */
    public String getName() {
        return name;
    }

    /**
     * Sets Name of the entity
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets Valuation of the entity
     */
    public float getValuation() {
        return valuation;
    }


}
