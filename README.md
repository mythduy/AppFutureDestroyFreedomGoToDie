# Android E-Commerce App - Backend Architecture (Room + MVVM)

## 📋 Tổng Quan

Ứng dụng bán linh kiện điện tử với **backend nội bộ** sử dụng **Room Database** và kiến trúc **MVVM** (Model-View-ViewModel).

### Công Nghệ Sử Dụng
- **Room Database**: Lưu trữ dữ liệu local
- **LiveData**: Reactive data updates
- **ViewModel**: Business logic layer
- **Repository Pattern**: Tách biệt data source
- **BCrypt**: Hash mật khẩu an toàn
- **Gson**: JSON serialization

---

## 🏗️ Cấu Trúc Project

```
com.example.ecommerce_app/
├── data/
│   ├── entities/          # Room Entities
│   │   ├── User.java
│   │   ├── Category.java
│   │   ├── Product.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Favorite.java
│   │   └── Review.java
│   │
│   ├── dao/              # Data Access Objects
│   │   ├── UserDao.java
│   │   ├── CategoryDao.java
│   │   ├── ProductDao.java
│   │   ├── CartItemDao.java
│   │   ├── OrderDao.java
│   │   ├── OrderItemDao.java
│   │   ├── FavoriteDao.java
│   │   └── ReviewDao.java
│   │
│   ├── converters/       # Type Converters
│   │   ├── DateConverter.java
│   │   └── StringListConverter.java
│   │
│   ├── database/         # Room Database
│   │   └── AppDatabase.java
│   │
│   └── repository/       # Repository Layer
│       ├── UserRepository.java
│       ├── ProductRepository.java
│       ├── CartRepository.java
│       └── OrderRepository.java
│
├── viewmodels/           # ViewModels (MVVM)
│   ├── AuthViewModel.java
│   ├── ProductListViewModel.java
│   ├── ProductDetailViewModel.java
│   ├── CartViewModel.java
│   └── CheckoutViewModel.java
│
└── utils/                # Utilities
    ├── PasswordHasher.java
    ├── ImageHelper.java
    └── SampleDataGenerator.java
```

---

## 🗄️ Database Schema

### Entities

1. **users** - Người dùng
   - id, username, email, passwordHash, fullName, phone, address, role, createdAt, updatedAt

2. **categories** - Danh mục
   - id, name, description, imageFilename, createdAt

3. **products** - Sản phẩm
   - id, name, description, price, stock, categoryId, imageFilenames (JSON), specifications, brand, sku, isActive, createdAt, updatedAt

4. **cart_items** - Giỏ hàng
   - id, userId, productId, quantity, addedAt

5. **orders** - Đơn hàng
   - id, userId, orderNumber, totalAmount, status, shippingAddress, shippingPhone, note, createdAt, updatedAt

6. **order_items** - Chi tiết đơn hàng
   - id, orderId, productId, quantity, price, subtotal

7. **favorites** - Sản phẩm yêu thích
   - id, userId, productId, addedAt

8. **reviews** - Đánh giá
   - id, userId, productId, rating, comment, createdAt, updatedAt

---

## 🚀 Cài Đặt và Chạy

### 1. Dependencies (đã cấu hình trong `build.gradle.kts`)

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")

// Lifecycle (ViewModel, LiveData)
implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

// Gson
implementation("com.google.code.gson:gson:2.10.1")

// BCrypt
implementation("at.favre.lib:bcrypt:0.10.2")

// Testing
androidTestImplementation("androidx.room:room-testing:2.6.1")
```

### 2. Sync Project

Chạy `Gradle Sync` để tải dependencies.

---

## 💾 Seed Dữ Liệu Mẫu

Database tự động seed dữ liệu khi chạy lần đầu tiên thông qua `AppDatabase.Callback`.

### Dữ Liệu Mẫu Bao Gồm:

**Users:**
- Admin: username=`admin`, password=`Admin@123`
- User 1: username=`user1`, password=`User@123`
- User 2: username=`user2`, password=`User@123`

**Categories:**
- Vi Điều Khiển
- Cảm Biến
- Module Truyền Thông
- Linh Kiện Điện Tử
- Module Hiển Thị

**Products:** (10 sản phẩm mẫu)
- Arduino Uno R3
- ESP32 DevKit V1
- DHT22 Cảm Biến
- HC-SR04 Siêu Âm
- NRF24L01+ Module RF
- OLED 0.96" I2C
- LED RGB 5mm
- Breadboard 830 điểm
- LCD 16x2 I2C
- Servo SG90 9g

---

## 🖼️ Quản Lý Ảnh từ Assets

### Cấu Trúc Thư Mục Assets

```
app/src/main/assets/
└── images/
    ├── products/
    │   ├── arduino_uno.jpg
    │   ├── arduino_uno_1.jpg
    │   ├── arduino_uno_2.jpg
    │   ├── esp32.jpg
    │   ├── esp32_1.jpg
    │   ├── dht22.jpg
    │   └── ... (các ảnh sản phẩm khác)
    │
    └── categories/
        ├── category_mcu.jpg
        ├── category_sensors.jpg
        └── ... (các ảnh danh mục)
```

### Load Ảnh trong Code

```java
// Load ảnh sản phẩm
Bitmap productImage = ImageHelper.loadImageFromAssets(context, "arduino_uno.jpg");

// Load gallery (nhiều ảnh)
List<String> imageFilenames = product.getImageFilenames();
List<Bitmap> gallery = ImageHelper.loadProductGallery(context, imageFilenames);

// Load ảnh category
Bitmap categoryImage = ImageHelper.loadCategoryImage(context, "category_mcu.jpg");
```

### Quy Ước Đặt Tên Ảnh

- **Ảnh chính**: `{sku}.jpg` (ví dụ: `arduino_uno.jpg`)
- **Ảnh phụ**: `{sku}_1.jpg`, `{sku}_2.jpg`, ...
- **Category**: `category_{name}.jpg`

---

## 📱 Sử Dụng trong Activity/Fragment

### 1. Authentication (Login/Register)

```java
public class LoginActivity extends AppCompatActivity {
    
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Observe current user
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // Login thành công, chuyển sang MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        // Observe error message
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Login button click
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            authViewModel.login(username, password);
        });
    }
}
```

### 2. Product List (Search & Filter)

```java
public class ProductListFragment extends Fragment {
    
    private ProductListViewModel viewModel;
    private ProductAdapter adapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
        
        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
        
        // Observe products
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });
        
        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchProducts(query);
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    viewModel.loadAllProducts();
                }
                return true;
            }
        });
        
        // Filter by category
        btnFilterCategory.setOnClickListener(v -> {
            viewModel.filterByCategory(selectedCategoryId);
        });
        
        return view;
    }
}
```

### 3. Product Detail

```java
public class ProductDetailActivity extends AppCompatActivity {
    
    private ProductDetailViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        // Get product ID from intent
        long productId = getIntent().getLongExtra("PRODUCT_ID", -1);
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        viewModel.loadProduct(productId);
        
        // Observe product
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                // Display product info
                tvName.setText(product.getName());
                tvPrice.setText(String.format("%,.0f đ", product.getPrice()));
                tvDescription.setText(product.getDescription());
                
                // Load images
                List<Bitmap> gallery = ImageHelper.loadProductGallery(
                    this, product.getImageFilenames()
                );
                imageGallery.setImages(gallery);
            }
        });
    }
}
```

### 4. Cart Management

```java
public class CartFragment extends Fragment {
    
    private CartViewModel viewModel;
    private long currentUserId;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        // Get current user ID (from AuthViewModel hoặc SharedPreferences)
        currentUserId = getCurrentUserId();
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        viewModel.setUserId(currentUserId);
        
        // Observe cart items
        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            adapter.setCartItems(cartItems);
            viewModel.calculateTotalPrice();
        });
        
        // Observe total price
        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), total -> {
            tvTotal.setText(String.format("%,.0f đ", total));
        });
        
        // Add to cart
        btnAddToCart.setOnClickListener(v -> {
            viewModel.addToCart(productId, quantity);
        });
        
        // Remove from cart
        btnRemove.setOnClickListener(v -> {
            viewModel.removeFromCart(productId);
        });
        
        // Checkout
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            intent.putExtra("TOTAL_AMOUNT", viewModel.getTotalPrice().getValue());
            startActivity(intent);
        });
        
        return view;
    }
}
```

### 5. Checkout Process

```java
public class CheckoutActivity extends AppCompatActivity {
    
    private CheckoutViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        // Get data from intent
        double totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        long userId = getCurrentUserId();
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        viewModel.setUserId(userId);
        viewModel.setTotalAmount(totalAmount);
        
        // Observe checkout success
        viewModel.getCheckoutSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                
                // Navigate to order detail
                Long orderId = viewModel.getCreatedOrderId().getValue();
                if (orderId != null) {
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra("ORDER_ID", orderId);
                    startActivity(intent);
                }
                finish();
            }
        });
        
        // Observe error
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Process checkout button
        btnPlaceOrder.setOnClickListener(v -> {
            String address = edtAddress.getText().toString();
            String phone = edtPhone.getText().toString();
            String note = edtNote.getText().toString();
            
            viewModel.setShippingAddress(address);
            viewModel.setShippingPhone(phone);
            viewModel.setNote(note);
            
            viewModel.processCheckout();
        });
    }
}
```

---

## 🧪 Testing

### Chạy Unit Tests

```bash
./gradlew test
```

### Chạy Android Tests (Database Tests)

```bash
./gradlew connectedAndroidTest
```

### Test Cases Có Sẵn

- `DatabaseTest.java`: Test CRUD operations cho User và Product DAOs
  - testInsertAndReadUser
  - testFindUserByUsername
  - testCheckUsernameExists
  - testUpdateUser
  - testDeleteUser
  - testInsertAndReadProduct
  - testSearchProductByName
  - testUpdateProductStock
  - testGetActiveProductCount

---

## 🔐 Security

### Password Hashing

Mật khẩu được hash bằng **BCrypt** với cost factor = 12:

```java
// Hash password khi đăng ký
String hashedPassword = PasswordHasher.hashPassword("User@123");

// Verify password khi đăng nhập
boolean isValid = PasswordHasher.verifyPassword("User@123", hashedPassword);

// Kiểm tra độ mạnh mật khẩu
boolean isStrong = PasswordHasher.isStrongPassword("User@123");
```

### Password Policy

- Tối thiểu 8 ký tự
- Ít nhất 3 trong 4 loại: chữ hoa, chữ thường, số, ký tự đặc biệt

---

## 📊 Database Migration

Khi cần thay đổi schema:

```java
// Trong AppDatabase.java
static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Thêm cột mới
        database.execSQL("ALTER TABLE products ADD COLUMN discount REAL DEFAULT 0");
    }
};

// Thêm migration vào builder
.addMigrations(MIGRATION_1_2)
```

---

## 🎯 Workflow Tổng Quan

### 1. User Flow
```
Register → Login → Browse Products → Search/Filter → 
View Detail → Add to Cart → Checkout → Place Order
```

### 2. Admin Flow
```
Login as Admin → Manage Products → Manage Orders → 
Update Stock → View Statistics
```

---

## 📝 Notes

1. **LiveData**: Tự động update UI khi data thay đổi
2. **Repository**: Tách biệt data logic khỏi ViewModel
3. **ExecutorService**: Background thread cho database operations
4. **Type Converters**: Convert complex types (Date, List<String>)
5. **Foreign Keys**: CASCADE delete để maintain data integrity

---

## 🚧 Roadmap / TODO

- [ ] Thêm pagination cho product list
- [ ] Implement order tracking
- [ ] Add notification system
- [ ] Integrate payment gateway
- [ ] Add product comparison feature
- [ ] Implement wishlist sync
- [ ] Add advanced search filters
- [ ] Create admin dashboard

---

## 📞 Support

Nếu có vấn đề, tham khảo:
- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [MVVM Architecture](https://developer.android.com/topic/architecture)
- [LiveData Guide](https://developer.android.com/topic/libraries/architecture/livedata)

---

**Happy Coding! 🚀**
