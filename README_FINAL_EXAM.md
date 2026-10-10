# คู่มือการใช้งานและเอกสารกำกับการส่งงานข้อสอบไฟนอล Android (Kotlin / MVVM / Compose)
**วิชา**: การพัฒนาแอปพลิเคชันสำหรับอุปกรณ์เคลื่อนที่ด้วย Android (Kotlin)
**ระดับการศึกษา**: ปริญญาตรี สาขาเทคโนโลยีสารสนเทศ (IT)
**ความเชื่อมโยง Git Repository**: `https://github.com/pajvi51-create/final131.git`

---

## 📌 โครงสร้างแอปพลิเคชัน (Application Overview)
แอปพลิเคชันนี้ถูกออกแบบโดยรวมงานทั้ง 2 ข้ออยู่ใน **แอปพลิเคชันเดียวกัน (Single App)** เพื่ออำนวยความสะดวกในการสอบสาธิตระบบ (Live Demo) ต่อหน้าผู้สอน สลับหน้าการทำงานได้อย่างลื่นไหลผ่าน **Bottom Navigation Bar** ด้านล่างของจอภาพ:

1. 📄 **Tab 1 - ข้อ 1 (.CSV)**: ระบบบันทึกรายรับ-รายจ่ายส่วนบุคคลด้วยไฟล์ CSV (15 คะแนน)
2. ☁️ **Tab 2 - ข้อ 2 (Supabase)**: ระบบจัดการสต็อกสินค้าชุมชนบน Cloud Database (15 คะแนน)
3. 📜 **Tab 3 - คำแนะนำ & SQL**: คำอธิบายการสอบสาธิต และ SQL Script สำหรับเตรียมตารางบน Supabase

---

## 🛠️ โครงสร้างสถาปัตยกรรมซอฟต์แวร์ (Architecture & Package Design)
ปฏิบัติตามหลักการ **MVVM (Model-View-ViewModel)** และ **Separation of Concerns** อย่างเคร่งครัด:

```
com.example.myapplication/
├── data/
│   ├── model/
│   │   ├── ExpenseItem.kt         # Data Class รายรับ-รายจ่าย + CSV Parser
│   │   └── Product.kt             # Data Class สต็อกสินค้า + @Serializable
│   └── repository/
│       ├── ExpenseRepository.kt     # Interface สำหรับไฟล์ CSV
│       ├── ExpenseRepositoryImpl.kt # File I/O บน context.filesDir (Internal Storage)
│       ├── ProductRepository.kt     # Interface สำหรับ Supabase Cloud CRUD
│       └── ProductRepositoryImpl.kt # สื่อสาร Supabase REST API (HttpURLConnection + Coroutines)
├── ui/
│   ├── expense/
│   │   ├── ExpenseViewModel.kt     # ViewModel คำนวณ Net Balance & File I/O บน Dispatchers.IO
│   │   └── ExpenseTrackerScreen.kt # Compose UI Material 3 (State Hoisting: Stateful/Stateless)
│   ├── stock/
│   │   ├── ProductStockViewModel.kt # ViewModel CRUD Operations บน Dispatchers.IO
│   │   └── ProductStockScreen.kt   # Compose UI (+1 / -1 Stock, Create, Delete, Read)
│   └── common/
│       └── InstructionScreen.kt    # หน้าแสดงคำสั่ง SQL Script และคู่มือการสอบนำเสนอ
└── MainActivity.kt                 # หน้าหลักรวม Bottom Navigation Bar
```

---

## 📖 คู่มือการใช้งานและการสอบสาธิต (Live Demo Guide)

### 1️⃣ ข้อที่ 1: ระบบบันทึกรายรับ-รายจ่าย (.CSV)

#### ขั้นตอนการทดสอบนำเสนอ:
1. กดเลือก **Tab 1: ข้อ 1 (.CSV)**
2. **การบันทึกข้อมูล (Append CSV)**:
   - กรอก **ชื่อรายการ** (เช่น `ค่าอาหารกลางวัน`, `เงินเดือน`)
   - กรอก **จำนวนเงิน** (เช่น `120.00`, `15000.00`)
   - เลือกประเภท **รายรับ** หรือ **รายจ่าย**
   - กดปุ่ม **บันทึกข้อมูล**
   - **ผลลัพธ์ที่ต้องตรวจสอบ**:
     - รายการจะถูกต่อท้าย (Append) ลงในไฟล์ `transactions.csv` ใน `context.filesDir`
     - ยอดเงินคงเหลือสุทธิ (Net Balance = รายรับรวม - รายจ่ายรวม) ถูกคำนวณและแสดงผลเรียลไทม์
     - รายการใหม่จะแสดงขึ้นในการ์ดประวัติย้อนหลังพร้อมประทับ วันที่ และ เวลา
     - รายรับแสดงเป็นสัญลักษณ์สีเขียว (`+`) / รายจ่ายแสดงเป็นสัญลักษณ์สีแดง (`-`)
3. **การทดสอบหมุนหน้าจอ (Configuration Change)**:
   - ทดสอบหมุนหน้าจอโทรศัพท์ (Portrait <-> Landscape)
   - **ผลลัพธ์**: ข้อมูลช่องกรอก ยอดคงเหลือ และรายการประวัติไม่สูญหาย (ViewModel Retained State)
4. **การลบข้อมูล (Delete File)**:
   - กดปุ่ม **ล้างข้อมูล**
   - **ผลลัพธ์**: ไฟล์ `transactions.csv` ถูกลบออกจาก Internal Storage ของเครื่องจริง ยอดคงเหลือรีเซ็ตเป็น `0.00 บาท` และรายการบนหน้าจอเคลียร์ทั้งหมด

---

### 2️⃣ ข้อที่ 2: ระบบจัดการสต็อกสินค้าชุมชนบน Cloud Database (Supabase CRUD)

#### การเตรียมความพร้อมบน Supabase (Database Setup):
1. เข้าไปที่ [Supabase SQL Editor](https://supabase.com) ในโปรเจกต์ของคุณ
2. คัดลอกคำสั่ง SQL ต่อไปนี้ไปวางและกด Run (สามารถคัดลอกผ่าน Tab 3 ในแอปได้เช่นกัน):
```sql
create table if not exists products (
  id bigint primary key generated always as identity,
  product_name text not null,
  stock_qty int not null default 0,
  price double precision not null default 0.0
);
```

#### ขั้นตอนการทดสอบนำเสนอ (CRUD 4 Operations):
1. กดเลือก **Tab 2: ข้อ 2 (Supabase)**
2. **ตั้งค่าการเชื่อมต่อ (Connection Settings)**:
   - กดปุ่ม **ตั้งค่า Cloud**
   - วาง **Supabase Project URL** (เช่น `https://xyzcompany.supabase.co`)
   - วาง **Supabase Anon Public Key**
3. **การอ่านข้อมูล (R - Read)**:
   - กดปุ่ม **เชื่อมต่อและโหลดรายการสินค้า** หรือ **รีเฟรช**
   - แอปจะยิงคำสั่ง `SELECT` ดึงรายการสินค้าทั้งหมดมาแสดงผลใน `LazyColumn` โดยเรียงจากรายการล่าสุด
4. **การเพิ่มสินค้าใหม่ (C - Create)**:
   - กรอก **ชื่อสินค้า** (เช่น `ข้าวฮักเมือง`), **จำนวนคงเหลือ** (เช่น `25`), และ **ราคา** (เช่น `150.00`)
   - กดปุ่ม **บันทึกเพิ่มสินค้าขึ้น Cloud**
   - **ผลลัพธ์**: แอปจะยิงคำสั่ง `INSERT` ไปยัง Supabase และโหลดรายการล่าสุดขึ้นมาแสดงทันที (ตรวจสอบบน Table Editor ของ Supabase ได้เรียลไทม์)
5. **การอัปเดตสต็อกสินค้า (U - Update)**:
   - ในการ์ดสินค้าแต่ละรายการ กดปุ่ม **`+1`** เพื่อเพิ่มสต็อก หรือ **`-1`** เพื่อลดสต็อก
   - **ผลลัพธ์**: แอปจะส่งคำสั่ง `UPDATE` ไปปรับเปลี่ยนค่า `stock_qty` บน Supabase ทันที
6. **การลบสินค้า (D - Delete)**:
   - ในการ์ดสินค้าที่ต้องการลบ กดปุ่ม **`ลบ`**
   - **ผลลัพธ์**: แอปจะส่งคำสั่ง `DELETE` ลบแถวข้อมูลออกจาก Supabase ตาม ID

---

## 🧪 การทดสอบยูนิตเทสต์ (Unit Testing)
โปรเจกต์นี้มี Unit Test ครอบคลุมการทำงานหลักใน `app/src/test/java/com/example/myapplication/`:
- **`ExpenseItemTest.kt`**: ทดสอบการแปลง Object <-> CSV string และระบบดักจับ Error
- **`ProductSerializationTest.kt`**: ทดสอบ JSON Serialization / Deserialization ของโครงสร้างสินค้า Supabase

สามารถรันคำสั่งทดสอบได้ผ่าน Gradle:
```powershell
./gradlew :app:testDebugUnitTest
```
**ผลการทดสอบ**: **6 passed, 0 failed**

---

## 📁 การตรวจสอบไฟล์ CSV ใน Device Explorer (สำหรับอาจารย์/ผู้สอน)
อาจารย์สามารถตรวจสอบไฟล์ CSV ที่แอปพลิเคชันสร้างขึ้นจริงได้ผ่าน Android Studio:
1. เปิดเมนู **Device File Explorer** ใน Android Studio (มุมขวาล่าง)
2. ไปที่พาธ: `/data/data/com.example.myapplication/files/transactions.csv`
3. ดับเบิลคลิกเปิดไฟล์ `transactions.csv` เพื่อตรวจสอบโครงสร้างบรรทัดข้อมูล CSV
