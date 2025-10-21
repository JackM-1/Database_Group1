package com.example.lab3databases;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText productNameInput, productPriceInput;
    Button addBtn, findBtn, deleteBtn;
    ListView productListView;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hook up everything from layout
        productNameInput = findViewById(R.id.productName);
        productPriceInput = findViewById(R.id.productPrice);
        addBtn = findViewById(R.id.addBtn);
        findBtn = findViewById(R.id.findBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        productListView = findViewById(R.id.productListView);

        dbHandler = new MyDBHandler(this);

        // button clicks
        addBtn.setOnClickListener(v -> addProduct());
        findBtn.setOnClickListener(v -> findProducts());
        deleteBtn.setOnClickListener(v -> deleteProduct());

        // show all products right away
        loadAllProducts();
    }

    private void addProduct() {
        String name = productNameInput.getText().toString().trim();
        String priceText = productPriceInput.getText().toString().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "enter both name and price", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // make new product and add to db
        Product product = new Product(name, price);
        dbHandler.addProduct(product);

        Toast.makeText(this, "product added", Toast.LENGTH_SHORT).show();
        productNameInput.setText("");
        productPriceInput.setText("");
        loadAllProducts();
    }

    private void loadAllProducts() {
        List<Product> products = dbHandler.getAllProducts();
        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        productListView.setAdapter(adapter);
    }

    private void findProducts() {
        String name = productNameInput.getText().toString().trim();
        String priceText = productPriceInput.getText().toString().trim();

        boolean hasName = !name.isEmpty();
        boolean hasPrice = !priceText.isEmpty();
        List<Product> results;

        if (hasName && hasPrice) {
            // both fields filled in
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }
            results = dbHandler.findProductsByNameAndPrice(name, price);
        } else if (hasName) {
            // only name entered
            results = dbHandler.findProductsByName(name);
        } else if (hasPrice) {
            // only price entered
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }
            results = dbHandler.findProductsByPrice(price);
        } else {
            // no filters, show all
            results = dbHandler.getAllProducts();
        }

        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        productListView.setAdapter(adapter);

        if (results.isEmpty()) {
            Toast.makeText(this, "no matches found", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        String name = productNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "enter product name to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        dbHandler.deleteProduct(name);
        Toast.makeText(this, "product deleted (if it existed)", Toast.LENGTH_SHORT).show();
        productNameInput.setText("");
        loadAllProducts();
    }
}
