# 📸 Hướng dẫn quản lý hình ảnh trong dự án

## 🗂️ Cấu trúc folder

```
app/src/main/assets/images/
├── products/
│   ├── product_1/              # Folder riêng cho sản phẩm ID = 1
│   │   ├── main.jpg           # Ảnh chính (bắt buộc)
│   │   ├── image_1.jpg        # Ảnh phụ 1
│   │   ├── image_2.jpg        # Ảnh phụ 2
│   │   ├── image_3.jpg        # Ảnh phụ 3
│   │   └── ...
│   ├── product_2/              # Folder riêng cho sản phẩm ID = 2
│   │   ├── main.jpg
│   │   ├── image_1.jpg
│   │   └── ...
│   ├── product_3/
│   │   └── main.jpg
│   └── ...
│
├── categories/
│   ├── electronics.jpg         # Ảnh cho category Electronics
│   ├── phones.jpg              # Ảnh cho category Phones
│   ├── computers.jpg
│   └── ...
│
└── banners/
    ├── banner_1.jpg            # Banner trang chủ 1
    ├── banner_2.jpg            # Banner trang chủ 2
    ├── banner_3.jpg
    └── ...
```

## 📋 Quy tắc đặt tên

### Products
- **Folder name**: `product_{productId}` (ví dụ: `product_1`, `product_123`)
- **Ảnh chính**: `main.jpg` (BẮT BUỘC - luôn phải có)
- **Ảnh phụ**: `image_1.jpg`, `image_2.jpg`, `image_3.jpg`, ...

### Categories
- Format: `{category_name}.jpg` (chữ thường, không dấu, không khoảng trắng)
- Ví dụ: `electronics.jpg`, `phones.jpg`, `computers.jpg`

### Banners
- Format: `banner_{number}.jpg`
- Ví dụ: `banner_1.jpg`, `banner_2.jpg`, `banner_3.jpg`

## 💻 Cách sử dụng trong code

### 1. Load ảnh chính của product

```java
// Cách 1: Load Bitmap trực tiếp
Bitmap bitmap = ImageHelper.loadProductMainImage(context, productId);
imageView.setImageBitmap(bitmap);

// Cách 2: Lấy path để dùng với Glide (KHUYẾN NGHỊ)
String imageUri = ImageHelper.getProductMainImageUri(productId);
Glide.with(context)
    .load(imageUri)
    .placeholder(R.drawable.placeholder)
    .into(imageView);
```

### 2. Load ảnh phụ của product

```java
// Load ảnh phụ thứ 1
String imageUri = ImageHelper.getProductImageUri(productId, "image_1.jpg");
Glide.with(context)
    .load(imageUri)
    .into(imageView);
```

### 3. Load tất cả ảnh của product (Gallery)

```java
// Lấy danh sách tất cả file ảnh trong folder product
String[] imageFiles = ImageHelper.getProductImageList(context, productId);

// Loop qua và load từng ảnh
for (String fileName : imageFiles) {
    String imageUri = ImageHelper.getProductImageUri(productId, fileName);
    // Load ảnh vào ViewPager hoặc RecyclerView
}
```

### 4. Load category image

```java
String imageUri = ImageHelper.getCategoryImageUri("electronics.jpg");
Glide.with(context)
    .load(imageUri)
    .into(imageView);
```

### 5. Load banner image

```java
String imageUri = ImageHelper.getBannerImageUri("banner_1.jpg");
Glide.with(context)
    .load(imageUri)
    .into(imageView);
```

### 6. Kiểm tra ảnh có tồn tại không

```java
String imagePath = ImageHelper.getProductMainImagePath(productId);
if (ImageHelper.imageExists(context, imagePath)) {
    // Ảnh tồn tại, load ảnh
} else {
    // Ảnh không tồn tại, hiển thị placeholder
}
```

## 🎨 Khuyến nghị

### Kích thước ảnh
- **Product main image**: 800x800px (tỉ lệ 1:1)
- **Product gallery**: 800x800px hoặc 1200x1200px
- **Category image**: 400x400px
- **Banner**: 1200x400px (tỉ lệ 3:1)

### Định dạng
- Format: JPG hoặc PNG
- Nén ảnh trước khi thêm vào assets để giảm dung lượng APK
- Tool đề xuất: TinyPNG, ImageOptim

### Best Practices
1. ✅ **LUÔN có file `main.jpg`** trong folder product
2. ✅ Đặt tên file theo đúng quy tắc (không dấu, chữ thường, không khoảng trắng)
3. ✅ Dùng Glide để load ảnh (tự động cache, resize)
4. ✅ Nén ảnh trước khi thêm vào assets
5. ❌ Không để file ảnh dư thừa trong assets

## 📦 Ví dụ thêm sản phẩm mới

Giả sử bạn thêm sản phẩm "Arduino Uno R3" với ID = 5:

1. Tạo folder: `app/src/main/assets/images/products/product_5/`
2. Thêm ảnh vào folder:
   - `main.jpg` (ảnh chính - BẮT BUỘC)
   - `image_1.jpg` (ảnh mặt sau)
   - `image_2.jpg` (ảnh chi tiết)
   - `image_3.jpg` (ảnh trong hộp)

3. Trong code, load ảnh:
```java
// Load ảnh chính
String mainImageUri = ImageHelper.getProductMainImageUri(5);
Glide.with(context).load(mainImageUri).into(imageView);

// Load gallery
String[] images = ImageHelper.getProductImageList(context, 5);
// images = ["main.jpg", "image_1.jpg", "image_2.jpg", "image_3.jpg"]
```

## 🔧 Troubleshooting

### Lỗi: Ảnh không hiển thị
- ✅ Kiểm tra đã có folder `product_{id}` chưa
- ✅ Kiểm tra file `main.jpg` có tồn tại không
- ✅ Kiểm tra tên file đúng format (chữ thường, không dấu)
- ✅ Clean & Rebuild project

### Lỗi: APK size quá lớn
- ✅ Nén ảnh trước khi thêm vào assets
- ✅ Xóa ảnh không dùng
- ✅ Cân nhắc chuyển sang Firebase Storage

## 🚀 Nâng cấp sau này

Khi dự án lớn hơn, nên migrate sang **Firebase Storage** hoặc **AWS S3**:
- Giảm APK size
- Cập nhật ảnh real-time
- Quản lý ảnh dễ dàng hơn

Xem file `FIREBASE_STORAGE_GUIDE.md` để biết cách migrate.
