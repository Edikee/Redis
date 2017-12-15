
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class main extends Application {
	Stage window;
	TableView<Product> table;
	TextField name, price, quantity, category;
	Button add, remove, clear, update;
	static Redis redis;
	int curentid;

	public static void main(String[] args) {

		launch(args);
		// redis = new Redis();

	}

	@Override
	public void start(Stage primarystage) throws Exception {
		window = primarystage;
		window.setTitle("test");
		redis = new Redis();

		TableColumn<Product, String> idColumn = new TableColumn<>("ID");
		idColumn.setMinWidth(200);
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setMinWidth(200);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Product, String> priceColumn = new TableColumn<>("Price");
		priceColumn.setMinWidth(200);
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		TableColumn<Product, String> quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setMinWidth(200);
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
		categoryColumn.setMinWidth(200);
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

		name = new TextField();
		name.setPromptText("name");
		name.setMinWidth(100);

		price = new TextField();
		price.setPromptText("price");
		price.setMinWidth(100);

		quantity = new TextField();
		quantity.setPromptText("quantity");
		quantity.setMinWidth(100);

		category = new TextField();
		category.setPromptText("category");
		category.setMinWidth(100);

		add = new Button("Add");
		add.setOnAction(e -> addButtonClicked());

		remove = new Button("Remove");
		remove.setOnAction(e -> removeButtonClicked());

		clear = new Button("Clear");
		clear.setOnAction(e -> ClearButtonClicked());

		update = new Button("Update");
		update.setOnAction(e -> UpdateButtonClicked());

		HBox hBox = new HBox();
		hBox.setPadding(new Insets(10, 10, 10, 10));
		hBox.setSpacing(10);
		hBox.getChildren().addAll(name, price, quantity, category, add, remove, update, clear);

		table = new TableView<>();
		table.setItems(getProduct());
		table.getColumns().addAll(idColumn, nameColumn, priceColumn, quantityColumn, categoryColumn);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(table, hBox);
		// table.setOnMouseClicked((MouseEvent event) -> mouseClick(event));
		Scene scene = new Scene(vBox);
		window.setScene(scene);
		window.show();

	}

	private void UpdateButtonClicked() {
		if (add.isVisible()) {

			ObservableList<Product> selectedItems = table.getSelectionModel().getSelectedItems();
			curentid = selectedItems.get(0).getId();
			name.setText(selectedItems.get(0).getName());
			price.setText(selectedItems.get(0).getPrice() + "");
			quantity.setText(selectedItems.get(0).getQuantity() + "");
			category.setText(selectedItems.get(0).getCategory());
			add.setVisible(false);
			remove.setVisible(false);
			table.setItems(getProduct());
		} else {
			add.setVisible(true);
			remove.setVisible(true);
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", name.getText());
			map.put("price", price.getText());
			map.put("quantity", quantity.getText());
			map.put("category", category.getText());
			redis.updateItem(curentid, map);
			table.setItems(getProduct());
		}
	}

	private void mouseClick(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			Product p = table.getSelectionModel().getSelectedItem();
			name.setText(p.getName());
			price.setText(p.getPrice() + "");
			quantity.setText(p.getQuantity() + "");
		}
	}

	private void ClearButtonClicked() {
		name.clear();
		price.clear();
		quantity.clear();
		category.clear();
		add.setVisible(true);
		remove.setVisible(true);
	}

	private void addButtonClicked() {
		Product laptop = new Product(name.getText(), Integer.parseInt(price.getText()),
				Integer.parseInt(quantity.getText()), category.getText());
		redis.addItem(laptop.getMap());
		table.setItems(getProduct());
		ClearButtonClicked();
	}

	private void removeButtonClicked() {
		ObservableList<Product> selected, allproduct;
		allproduct = table.getItems();
		selected = table.getSelectionModel().getSelectedItems();
		redis.removeItem(selected.get(0).getId());
		table.setItems(getProduct());
	}

	// get all of the laptop
	public ObservableList<Product> getProduct() {
		ObservableList<Product> list = FXCollections.observableArrayList();
		List<Map<String, String>> productlist = redis.getProduct();
		for (int i = 0; i < productlist.size(); i++) {
			Product p = new Product();
			p.setAll(productlist.get(i));
			list.add(p);
		}

		return list;

	}

}
