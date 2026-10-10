# คู่มือการพัฒนาอย่างละเอียดและรายการไฟล์สำหรับส่งอาจารย์ (Final Exam Guide)
**นักศึกษา**: ปาจรีย์ สุคนธชาติ
**รหัสนักศึกษา**: 67102122131
**Git Repository**: `https://github.com/pajvi51-create/final131.git`

---

## 📘 ส่วนที่ 1: คู่มือการทำอย่างละเอียด (Step-by-Step Development Guide)

### ขั้นตอนที่ 1: การตั้งค่าพึ่งพา (Dependencies Setup)
1. กำหนดปลั๊กอิน `kotlin.serialization` ใน `build.gradle.kts` (Top-level) และ `app/build.gradle.kts`
2. เพิ่มไลบรารี `kotlinx-serialization-json` สำหรับแปลงโครงสร้าง JSON จาก Supabase REST API
3. เปิดใช้งาน `buildFeatures { compose = true }` และ Material 3 Compose

### ขั้นตอนที่ 2: การจัดวางโครงสร้างแพ็กเกจ (Package Architecture)
แยกแพ็กเกจตามสถาปัตยกรรม MVVM และ Clean Architecture:
- `data/model/` : เก็บข้อมูลและโครงสร้างข้อมูล
- `data/repository/` : เก็บ Repository Interface และคลาสจัดการ File I/O / REST API
- `ui/expense/` : View และ ViewModel สำหรับข้อที่ 1 (รายรับ-รายจ่าย CSV)
- `ui/stock/` : View และ ViewModel สำหรับข้อที่ 2 (สต็อกสินค้า Supabase)
- `ui/common/` : หน้าคู่มือและ SQL Script
- `MainActivity.kt` : หน้าหลักรวม Navigation Bar

---

### ขั้นตอนที่ 3: การพัฒนาข้อที่ 1 (ระบบบันทึกรายรับ-รายจ่าย .CSV)
1. **สร้าง Data Class `ExpenseItem`**:
   - กำหนดฟิลด์ `date`, `time`, `name`, `amount`, `type` (`INCOME` / `EXPENSE`)
   - เพิ่มฟังก์ชัน `toCsvRow()` แปลง Object เป็นบรรทัด CSV
   - เพิ่ม `ExpenseItem.fromCsvRow(line)` อ่านบรรทัด CSV แปลงกลับเป็น Object
2. **สร้าง `ExpenseRepository` & `ExpenseRepositoryImpl`**:
   - บันทึกข้อมูลแบบต่อท้าย (Append) ลงไฟล์ `transactions.csv` บน `context.filesDir` (Internal Storage)
   - อ่านไฟล์แบบ Line-by-line แปลงเป็น List<ExpenseItem>
   - เพิ่มฟังก์ชัน `clearAllData()` เพื่อลบไฟล์ CSV และเคลียร์ค่า
3. **สร้าง `ExpenseViewModel`**:
   - สืบทอด `AndroidViewModel`
   - รันงานเบื้องหลังบน `Dispatchers.IO` ผ่าน `viewModelScope.launch`
   - คำนวณยอดเงินคงเหลือสุทธิ = รายรับรวม - รายจ่ายรวม
   - ถือครอง UI State ข้อมูลไม่สูญหายเมื่อหมุนหน้าจอ
4. **สร้าง `ExpenseTrackerScreen`**:
   - ใช้หลัก State Hoisting แยก Stateful (`ExpenseTrackerScreen`) และ Stateless (`ExpenseTrackerContent`)
   - รองรับ **Adaptive Landscape**: แนวตั้งเรียงแนวตั้ง / แนวนอนแยกเป็น 2 คอลัมน์ขนานกัน (ฝั่งซ้ายฟอร์มกรอก / ฝั่งขวา LazyColumn ประวัติ)

---

### ขั้นตอนที่ 4: การพัฒนาข้อที่ 2 (ระบบจัดการสต็อกสินค้าชุมชน Supabase CRUD)
1. **เตรียมตารางบน Supabase**:
   - รันคำสั่ง SQL สร้างตาราง `products` (`id`, `product_name`, `stock_qty`, `price`)
2. **สร้าง Data Class `Product`**:
   - ติดตั้งแอนโนเทชัน `@Serializable` และ `@SerialName` ให้ตรงกับฟิลด์ตาราง
3. **สร้าง `ProductRepository` & `ProductRepositoryImpl`**:
   - พัฒนา REST API Connector ด้วย `HttpURLConnection` ครบทั้ง 4 ด้าน:
     - **Create**: `POST /rest/v1/products`
     - **Read**: `GET /rest/v1/products?select=*&order=id.desc`
     - **Update**: `POST` ร่วมกับ `X-HTTP-Method-Override: PATCH` แก้ไข `stock_qty`
     - **Delete**: `DELETE /rest/v1/products?id=eq.<id>`
4. **สร้าง `ProductStockViewModel`**:
   - รัน Network Calls บน `Dispatchers.IO`
   - จัดการฟังก์ชัน `loadProducts()`, `addProduct()`, `increaseStock()`, `decreaseStock()`, `deleteProduct()`
5. **สร้าง `ProductStockScreen`**:
   - รองรับ **Adaptive Landscape** (ฝั่งซ้ายตั้งค่า Cloud + เพิ่มสินค้า / ฝั่งขวา LazyColumn สต็อก)
   - ปุ่มกดปรับสต็อก `+1` และ `-1` เรียลไทม์

---

### ขั้นตอนที่ 5: การรวมระบบและสร้างคู่มือในตัวแอป
1. สร้างหน้า `InstructionScreen.kt` แสดงคำอธิบายและปุ่มคัดลอก SQL Script
2. รวมทั้ง 3 หน้าจอบน `MainActivity.kt` ด้วย `NavigationBar` (Bottom Navigation Tabs)

---

## 📦 ส่วนที่ 2: รายการไฟล์ที่ต้องส่งอาจารย์ (Deliverables List)

### 1. ลิงก์ Git Repository หลัก
- **URL**: [https://github.com/pajvi51-create/final131.git](https://github.com/pajvi51-create/final131.git) (Branch: `main`)

### 2. ซอร์สโค้ดโปรเจกต์ (Source Code Files)
อาจารย์สามารถตรวจซอร์สโค้ดจากไฟล์หลักเหล่านี้:
- 📄 `app/src/main/java/com/example/myapplication/data/model/ExpenseItem.kt`
- 📄 `app/src/main/java/com/example/myapplication/data/model/Product.kt`
- 📄 `app/src/main/java/com/example/myapplication/data/repository/ExpenseRepository.kt`
- 📄 `app/src/main/java/com/example/myapplication/data/repository/ExpenseRepositoryImpl.kt`
- 📄 `app/src/main/java/com/example/myapplication/data/repository/ProductRepository.kt`
- 📄 `app/src/main/java/com/example/myapplication/data/repository/ProductRepositoryImpl.kt`
- 📄 `app/src/main/java/com/example/myapplication/ui/expense/ExpenseViewModel.kt`
- 📄 `app/src/main/java/com/example/myapplication/ui/expense/ExpenseTrackerScreen.kt`
- 📄 `app/src/main/java/com/example/myapplication/ui/stock/ProductStockViewModel.kt`
- 📄 `app/src/main/java/com/example/myapplication/ui/stock/ProductStockScreen.kt`
- 📄 `app/src/main/java/com/example/myapplication/ui/common/InstructionScreen.kt`
- 📄 `app/src/main/java/com/example/myapplication/MainActivity.kt`

### 3. ไฟล์การตั้งค่าระบบ (Configuration & Build Files)
- ⚙️ `app/build.gradle.kts`
- ⚙️ `build.gradle.kts`
- ⚙️ `gradle/libs.versions.toml`
- ⚙️ `app/src/main/AndroidManifest.xml`

### 4. ไฟล์คู่มือและเอกสารประกอบ (Documentation Files)
- 📝 `README.md` (ไฟล์แรกบน GitHub)
- 📝 `README_FINAL_EXAM.md` (คู่มือการใช้งานและคำอธิบายฉบับเต็ม)
- 📝 `DETAILED_STEP_BY_STEP_GUIDE.md` (คู่มือการพัฒนาอย่างละเอียด)
- 📝 `submission/README_ส่งงาน.md` (เอกสารสรุปส่งงานภาษาไทย)
