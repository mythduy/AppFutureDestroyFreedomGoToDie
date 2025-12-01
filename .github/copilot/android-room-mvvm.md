# 📱 Android: MVVM + Room — Hướng dẫn chi tiết

**Mục đích:** Chuẩn hóa cách cài đặt Entity, DAO, Database, Repository, ViewModel, LiveData.

## Cấu trúc thư mục gợi ý
com.yourname.electronicsstore/
models/
dao/
database/
repository/
viewmodel/
ui/ # activities/fragments/adapters
utils/
constants/

## Entities (ví dụ Product)
```java
/**
 * Product entity - lưu thông tin sản phẩm
 */
@Entity(tableName = "products")
public class Product {
  @PrimaryKey(autoGenerate = true)
  public int id;
  @ColumnInfo(name = "name")
  public String name;
  @ColumnInfo(name = "description")
  public String description;
  @ColumnInfo(name = "price")
  public double price;
  @ColumnInfo(name = "image_file") // lưu tên file trong assets
  public String imageFile;
  @ColumnInfo(name = "stock")
  public int stock;
  @ColumnInfo(name = "category_id")
  public int categoryId;
}
DAO (ví dụ search & pagination)
java
Copy code
@Dao
public interface ProductDao {
  @Insert
  long insert(Product p);
  @Query("SELECT * FROM products WHERE name LIKE :q OR description LIKE :q ORDER BY name ASC")
  LiveData<List<Product>> search(String q);
  @Query("SELECT * FROM products WHERE category_id = :catId AND price BETWEEN :min AND :max")
  LiveData<List<Product>> filter(int catId, double min, double max);
}

Database:
Singleton RoomDatabase.
Converter: Date ↔ Long, List<String> ↔ JSON (Gson).
Seed sample data trong RoomDatabase.Callback.onCreate.

Repository:
Interface + Impl (nếu cần mocking).
Kết hợp nguồn dữ liệu (Room, remote nếu có).

ViewModel:
Expose LiveData cho UI.
Xử lý business logic (validate, calculate totals).

Assets images:
Quy ước: assets/images/products/product_<id>.jpg, product_<id>_1.jpg, ...
Dùng AssetManager để liệt kê.

Migration
Thêm migration template (v1->v2) trong database/ folder.

Test
DAO androidTest (Room in-memory).
ViewModel unit test (mock repository).
