# 📋 Hướng dẫn lấy danh sách sản phẩm và chuẩn bị hình ảnh

## 🎯 Tổng quan

Database hiện tại có **10 sản phẩm** linh kiện điện tử. Bạn cần tìm và thêm hình ảnh cho từng sản phẩm.

## 📦 Danh sách sản phẩm trong database

### 1. Arduino Uno R3
- **ID**: 1
- **SKU**: ARDUINO_UNO_R3
- **Brand**: Arduino
- **Giá**: 250,000 VNĐ
- **Folder**: `products/product_1/`
- **Keywords tìm ảnh**: "Arduino Uno R3", "Arduino Uno board"

### 2. ESP32 DevKit V1
- **ID**: 2
- **SKU**: ESP32_DEVKIT_V1
- **Brand**: Espressif
- **Giá**: 150,000 VNĐ
- **Folder**: `products/product_2/`
- **Keywords**: "ESP32 DevKit", "ESP32 development board"

### 3. DHT22 Cảm Biến Nhiệt Độ Độ Ẩm
- **ID**: 3
- **SKU**: DHT22_SENSOR
- **Brand**: Generic
- **Giá**: 85,000 VNĐ
- **Folder**: `products/product_3/`
- **Keywords**: "DHT22 sensor", "temperature humidity sensor"

### 4. HC-SR04 Cảm Biến Siêu Âm
- **ID**: 4
- **SKU**: HC_SR04_ULTRASONIC
- **Brand**: Generic
- **Giá**: 35,000 VNĐ
- **Folder**: `products/product_4/`
- **Keywords**: "HC-SR04", "ultrasonic sensor"

### 5. NRF24L01+ Module RF
- **ID**: 5
- **SKU**: NRF24L01_PLUS
- **Brand**: Nordic
- **Giá**: 45,000 VNĐ
- **Folder**: `products/product_5/`
- **Keywords**: "NRF24L01", "RF module 2.4GHz"

### 6. OLED 0.96 inch I2C
- **ID**: 6
- **SKU**: OLED_096_I2C
- **Brand**: Generic
- **Giá**: 75,000 VNĐ
- **Folder**: `products/product_6/`
- **Keywords**: "OLED 0.96 inch", "OLED display I2C"

### 7. LED RGB 5mm Common Cathode
- **ID**: 7
- **SKU**: LED_RGB_5MM_CC
- **Brand**: Generic
- **Giá**: 5,000 VNĐ
- **Folder**: `products/product_7/`
- **Keywords**: "LED RGB 5mm", "RGB LED common cathode"

### 8. Breadboard 830 điểm
- **ID**: 8
- **SKU**: BREADBOARD_830
- **Brand**: Generic
- **Giá**: 30,000 VNĐ
- **Folder**: `products/product_8/`
- **Keywords**: "Breadboard 830", "solderless breadboard"

### 9. LCD 16x2 I2C
- **ID**: 9
- **SKU**: LCD_16X2_I2C
- **Brand**: Generic
- **Giá**: 65,000 VNĐ
- **Folder**: `products/product_9/`
- **Keywords**: "LCD 16x2 I2C", "LCD display 1602"

### 10. Servo SG90 9g
- **ID**: 10
- **SKU**: SERVO_SG90
- **Brand**: TowerPro
- **Giá**: 40,000 VNĐ
- **Folder**: `products/product_10/`
- **Keywords**: "Servo SG90", "micro servo motor"

---

## 🔧 CÁCH 1: Export tự động (KHUYẾN NGHỊ)

### Bước 1: Thêm DebugActivity vào AndroidManifest.xml

Mở file `app/src/main/AndroidManifest.xml` và thêm:

```xml
<activity
    android:name=".DebugActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### Bước 2: Chạy app và mở DebugActivity

1. Build và install app
2. Mở DebugActivity (sẽ xuất hiện icon mới trên launcher)
3. Click button "📄 Export Product List (TXT)"

### Bước 3: Lấy file

File sẽ được lưu tại:
```
/sdcard/Android/data/com.example.ecommerce_app/files/product_list.txt
```

**Cách xem:**
1. Mở **File Manager** trên điện thoại
2. Vào **Internal Storage > Android > data > com.example.ecommerce_app > files**
3. Mở file `product_list.txt`
4. Copy file ra máy tính để dễ xem

---

## 📝 CÁCH 2: Xem trực tiếp trong code

Mở file: `app/src/main/java/com/example/ecommerce_app/utils/SampleDataGenerator.java`

Tìm method `createSampleProducts()` - Ở đó có đầy đủ thông tin 10 sản phẩm.

---

## 🖼️ Hướng dẫn tìm và thêm hình ảnh

### Nguồn tìm ảnh MIỄN PHÍ:

1. **Google Images** (Advanced Search > Usage Rights > Free to use)
2. **Unsplash.com** - Ảnh chất lượng cao miễn phí
3. **Pexels.com** - Stock photos miễn phí
4. **Arduino.cc** - Ảnh chính thức Arduino
5. **SparkFun.com** - Thư viện linh kiện
6. **Adafruit.com** - Learning resources với ảnh đẹp

### Nguồn ảnh sản phẩm (có thể lấy làm reference):

1. **AliExpress** - Ảnh sản phẩm chất lượng tốt
2. **Shopee/Lazada** - Ảnh local
3. **Datasheet** - Ảnh chính thức từ nhà sản xuất

### Yêu cầu ảnh:

- **Kích thước**: 800x800px (tỉ lệ 1:1)
- **Format**: JPG hoặc PNG
- **Nền**: Trắng hoặc trong suốt
- **Dung lượng**: < 200KB (sau khi nén)

### Công cụ chỉnh sửa/nén ảnh:

1. **TinyPNG** - https://tinypng.com/ (nén online)
2. **GIMP** - Free image editor (resize + crop)
3. **Paint.NET** - Windows
4. **Photopea** - https://photopea.com (Photoshop online miễn phí)

---

## 📁 Tạo folder structure

### Cách 1: Tự động (PowerShell)

Copy script này vào PowerShell:

```powershell
cd "c:\Users\MKhang\Desktop\AppSieuCap"
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_1" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_2" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_3" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_4" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_5" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_6" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_7" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_8" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_9" -Force
New-Item -ItemType Directory -Path "app\src\main\assets\images\products\product_10" -Force
```

### Cách 2: Tạo thủ công

Tạo folder theo cấu trúc:
```
app/src/main/assets/images/products/
├── product_1/
├── product_2/
├── product_3/
├── product_4/
├── product_5/
├── product_6/
├── product_7/
├── product_8/
├── product_9/
└── product_10/
```

---

## 📥 Thêm ảnh vào folder

Với mỗi product, thêm ít nhất **1 ảnh chính** tên `main.jpg`:

```
product_1/
├── main.jpg          ← BẮT BUỘC (ảnh chính)
├── image_1.jpg       ← Tùy chọn (ảnh góc khác)
├── image_2.jpg       ← Tùy chọn (ảnh chi tiết)
└── image_3.jpg       ← Tùy chọn (ảnh trong hộp)
```

---

## ✅ Checklist

- [ ] Tạo 10 folder cho 10 sản phẩm
- [ ] Tìm và download ảnh cho từng sản phẩm
- [ ] Resize ảnh về 800x800px
- [ ] Nén ảnh để giảm dung lượng
- [ ] Đổi tên ảnh chính thành `main.jpg`
- [ ] Copy ảnh vào đúng folder
- [ ] Clean & Rebuild project
- [ ] Test app xem ảnh hiển thị đúng

---

## 🚀 Sau khi thêm ảnh xong

1. **Clean Project**: Build > Clean Project
2. **Rebuild**: Build > Rebuild Project
3. **Run app** và kiểm tra ảnh hiển thị

---

## 💡 Tips

1. **Ảnh Arduino/ESP32**: Tìm trên arduino.cc, espressif.com (ảnh chính thức)
2. **Ảnh cảm biến**: Tìm trên sparkfun.com, adafruit.com
3. **Ảnh generic**: Google Images với filter "free to use"
4. **Nhanh hơn**: Lấy ảnh từ AliExpress (ảnh chất lượng cao, đa góc độ)
5. **Nền trắng**: Dùng remove.bg để xóa nền nếu cần

---

## 📞 Cần export file?

Nếu muốn export file chi tiết hơn, chạy DebugActivity và click:
- **📄 Export TXT** - File text chi tiết
- **📊 Export CSV** - Import vào Excel
- **🗂️ Export Script** - Script tạo folder tự động

File sẽ được lưu tại:
```
/sdcard/Android/data/com.example.ecommerce_app/files/
```
