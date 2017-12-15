import java.util.HashMap;
import java.util.Map;

public class Product {
	private int id;
	private String name;
	private int price;
	private int quantity;
	private String category;

	public Product(String name, int price, int quantity, String category) {

		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.category = category;
	}

	public Product() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}
	public String getCategory() {
		return category;
	}
	public void setId(int i){
		
	}
	public int getId() {
		return id;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
 
	public void setAll(Map<String, String> map) {

		this.id = Integer.parseInt(map.get("id"));
		this.name = map.get("name");
		this.price = Integer.parseInt(map.get("price"));
		this.quantity = Integer.parseInt(map.get("quantity"));
		this.category = map.get("category");
	}

	public Map<String, String> getMap() {
		Map m = new HashMap<String, String>();
		m.put("id",id);
		m.put("name", name);
		m.put("price", price + "");
		m.put("quantity", quantity + "");
		m.put("category", category);
		return m;
	}
	public String toString(){
		return id + " " + name + " " +price + " " + quantity + " " + category;
	}
}
