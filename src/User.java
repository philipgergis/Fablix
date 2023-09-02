import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private HashMap<String, Integer> cartInfo;
    private HashMap<String, String> idToTitle;

    public User(String username) {
        this.username = username;
        this.cartInfo = new HashMap<>();
        this.idToTitle = new HashMap<>();
    }

    public void newCart()
    {
        cartInfo = new HashMap<>();
        idToTitle = new HashMap<>();
    }

    public void addTitle(String id, String title)
    {
        if(!idToTitle.containsKey(id))
        {
            idToTitle.put(id, title);
        }
    }

    public void updateCart(String id, int quantity)
    {
        if(quantity <= 0)
        {
            cartInfo.remove(id);
        }
        else
        {
            cartInfo.put(id, quantity);
        }
    }

    public Integer getQuantity(String id)
    {
        if(cartInfo.containsKey(id))
        {
            return cartInfo.get(id);
        }
        else
        {
            return -1;
        }
    }

    public String getTitle(String id)
    {
        if(idToTitle.containsKey(id))
        {
            return idToTitle.get(id);
        }
        else
        {
            return null;
        }
    }

    public HashMap<String, Integer> getCart()
    {
        return cartInfo;
    }
}
