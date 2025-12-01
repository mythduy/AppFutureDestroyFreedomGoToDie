# 🎨 Quy ước lưu trữ hình ảnh sản phẩm (Assets)

## 🎯 Mục tiêu
Tổ chức thư mục ảnh sạch, dễ mở rộng, tránh trùng tên file, hỗ trợ gallery nhiều ảnh cho mỗi sản phẩm.

Mỗi sản phẩm sẽ có **một thư mục riêng**, bên trong chứa toàn bộ hình ảnh của sản phẩm đó.

---

# 📁 1. Cấu trúc thư mục (NEW – mỗi product một folder)

Tất cả ảnh sẽ nằm trong:

products/
1/
main.jpg
1.jpg
2.jpg
thumbnail.jpg
2/
main.jpg
1.jpg
3/
main.webp
angle.jpg


📌 **Quy ước:**
- Tên folder = **productId** của sản phẩm (giống trong DB).
- Mỗi folder có **ít nhất 1 ảnh chính**:
  - `main.jpg` hoặc `main.webp`
- Các ảnh khác:
  - `1.jpg`, `2.jpg`
  - hoặc `detail_1.jpg`, `detail_2.jpg`
- Format ưu tiên: **WebP** → nhỏ hơn, nhẹ hơn, đẹp hơn.

---

# 🔎 2. Cách load ảnh trong code (gợi ý chuẩn)

## 🔸 2.1. Lấy danh sách ảnh gallery của một sản phẩm

```java
public static List<String> loadProductImages(Context context, int productId) {
    List<String> results = new ArrayList<>();

    try {
        String folderPath = "products/" + productId;
        AssetManager am = context.getAssets();
        String[] files = am.list(folderPath);

        if (files != null) {
            for (String name : files) {
                results.add("file:///android_asset/" + folderPath + "/" + name);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return results;
}

3. Quy tắc đặt tên ảnh

Mỗi sản phẩm bắt buộc có:
Tệp	Ý nghĩa
main.jpg	Ảnh chính
1.jpg, 2.jpg	Ảnh phụ
thumbnail.jpg	(Tùy chọn) Thumbnail dùng cho Home/Product List