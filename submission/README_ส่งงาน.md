# เอกสารส่งงานและคู่มือการนำเสนอข้อสอบไฟนอล Android
**ชื่อ-นามสกุล**: ปาจรีย์ สุคนธชาติ
**รหัสนักศึกษา**: 67102122131
**Git Repository**: `https://github.com/pajvi51-create/final131.git`

---

## สรุปเนื้อหาและสิ่งที่ส่งมอบ

1. **ซอร์สโค้ดโปรเจกต์ Android (Kotlin / Jetpack Compose / MVVM)**:
   - รวมการทำงานข้อที่ 1 (บันทึกรายรับ-รายจ่ายไฟล์ .CSV) และข้อที่ 2 (ระบบจัดการสต็อกสินค้าบน Supabase Cloud Database) ไว้ในแอปพลิเคชันเดียวกัน
   - สลับหน้าการทำงานเรียบลื่นด้วย Bottom Navigation Bar

2. **การปฏิบัติตามเกณฑ์การประเมิน (Scoring Rubric - 30 คะแนนเต็ม)**:
   - **สถาปัตยกรรม MVVM & Repository Pattern**: แยก Layer สม่ำเสมอ ไม่เขียน File I/O หรือ Network Request ไว้ใน UI
   - **Coroutines & Thread Safety**: ใช้ `viewModelScope.launch` และ `Dispatchers.IO` ทำงานเบื้องหลัง ไม่ทำให้ UI Freeze
   - **Configuration Change**: ข้อมูลและ UI State ไม่สูญหายเมื่อหมุนหน้าจอ
   - **File I/O (.CSV)**: บันทึกข้อมูลแบบ Append ลงใน `context.filesDir` อ่านบรรทัดต่อบรรทัด และลบไฟล์จริงออกจากระบบ
   - **Supabase Cloud Database**: รองรับ CRUD ครบทั้ง 4 ด้าน (`Create`, `Read`, `Update +1/-1`, `Delete`)

3. **คู่มือการทดสอบนำเสนอ**:
   - อ่านรายละเอียดคู่มืออย่างเป็นทางการได้ในไฟล์ `README_FINAL_EXAM.md` หรือเปิดดูใน Tab "คำแนะนำ & SQL" ภายในตัวแอปพลิเคชัน
