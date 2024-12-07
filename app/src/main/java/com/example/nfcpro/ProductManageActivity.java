package com.example.nfcpro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.nfcpro.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductManageActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextInputEditText nameInput, priceInput, stockInput;
    private Button submitButton;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private String productId;
    private String boothId;
    private Uri selectedImageUri;
    private SessionManager sessionManager;
    private boolean isEditMode = false;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    productImage.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manage);

        initializeViews();
        setupFirebase();
        loadSessionData();
        checkEditMode();
        setupListeners();
    }

    private void initializeViews() {
        ImageButton backButton = findViewById(R.id.backButton);
        productImage = findViewById(R.id.productImage);
        CardView imageContainer = findViewById(R.id.imageContainer);
        nameInput = findViewById(R.id.productNameInput);
        priceInput = findViewById(R.id.productPriceInput);
        stockInput = findViewById(R.id.productStockInput);
        submitButton = findViewById(R.id.submitButton);

        backButton.setOnClickListener(v -> finish());
        imageContainer.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void setupFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference().child("nfcpro");
        storageRef = FirebaseStorage.getInstance().getReference().child("product_images");
    }

    private void loadSessionData() {
        sessionManager = new SessionManager(this);
        SessionManager.SessionData sessionData = sessionManager.getSession();
        if (sessionData != null) {
            boothId = sessionData.getBoothId();
        } else {
            finish();
        }
    }

    private void checkEditMode() {
        productId = getIntent().getStringExtra("PRODUCT_ID");
        isEditMode = productId != null;

        if (isEditMode) {
            submitButton.setText("수정하기");
            loadProductData();
        }
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                if (isEditMode) {
                    updateProduct();
                } else {
                    createProduct();
                }
            }
        });
    }

    private void loadProductData() {
        databaseRef.child("products").child(productId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        nameInput.setText(snapshot.child("name").getValue(String.class));
                        priceInput.setText(String.valueOf(snapshot.child("price").getValue(Long.class)));
                        stockInput.setText(String.valueOf(snapshot.child("stock").getValue(Long.class)));

                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                        // 이미지 로딩 로직 추가
                    }
                })
                .addOnFailureListener(e -> showError("상품 정보를 불러오는데 실패했습니다"));
    }

    private void createProduct() {
        String newProductId = UUID.randomUUID().toString();
        uploadProductData(newProductId, true);
    }

    private void updateProduct() {
        uploadProductData(productId, false);
    }

    private void uploadProductData(String id, boolean isNew) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", nameInput.getText().toString());
        productData.put("price", Long.parseLong(priceInput.getText().toString()));
        productData.put("stock", Long.parseLong(stockInput.getText().toString()));
        productData.put("boothId", boothId);
        productData.put("isAvailable", true);

        if (selectedImageUri != null) {
            StorageReference imageRef = storageRef.child(id + ".jpg");
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                productData.put("imageUrl", uri.toString());
                                saveProductToDatabase(id, productData, isNew);
                            }))
                    .addOnFailureListener(e -> showError("이미지 업로드에 실패했습니다"));
        } else {
            saveProductToDatabase(id, productData, isNew);
        }
    }

    private void saveProductToDatabase(String productId, Map<String, Object> productData, boolean isNew) {
        DatabaseReference productRef = databaseRef.child("products").child(productId);
        productRef.setValue(productData)
                .addOnSuccessListener(aVoid -> {
                    if (isNew) {
                        // 부스의 상품 목록에 추가
                        databaseRef.child("booth_products")
                                .child(boothId)
                                .child(productId)
                                .setValue(true);
                    }
                    finish();
                })
                .addOnFailureListener(e -> showError("상품 저장에 실패했습니다"));
    }

    private boolean validateInputs() {
        if (nameInput.getText().toString().isEmpty()) {
            showError("상품명을 입력해주세요");
            return false;
        }
        if (priceInput.getText().toString().isEmpty()) {
            showError("가격을 입력해주세요");
            return false;
        }
        if (stockInput.getText().toString().isEmpty()) {
            showError("재고를 입력해주세요");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}