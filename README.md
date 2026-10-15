# Chain Break (Epic Fight Passive) — Forge 1.20.1

พาสซีฟสำหรับ Epic Fight: ตีโดนศัตรูเพื่อเติมหลอด → หลอดเต็มแล้วกดปุ่ม (ค่าเริ่มต้น **Caps Lock**) เพื่อ

- เล่นท่ากลิ้งของ Epic Fight (`BIPED_ROLL_FORWARD` / `BIPED_ROLL_BACKWARD`) ทันที
- หลุดคอมโบทันที (ท่าโจมตีที่กำลังเล่นถูกแทนที่ + รีเซ็ตตัวนับคอมโบ)
- ไม่โดนดาเมจระหว่างกลิ้ง (ยกเว้น void / `/kill`)
- ถ้าโดนสตั้น/ล้มระหว่างกลิ้ง ท่ากลิ้งหลุดและอมตะหมดทันที
- ไม่มีคูลดาวน์ — ใช้ได้เมื่อหลอดเต็มเท่านั้น (ค่าเริ่มต้น 50 ฮิตต่อ 1 ครั้ง)
- จอแฟลชขาว + texture พุ่งกลางจอ + กล้องสั่นตอนใช้

กดปุ่มพร้อมกด W/A/S/D เพื่อเลือกทิศกลิ้ง (กด S = กลิ้งถอยหลัง, ไม่กดอะไร = กลิ้งไปข้างหน้า)

## ตั้งค่า

- เปลี่ยนปุ่ม: Options → Controls → หมวด **Chain Break**
- `config/chainbreak-common.toml`: `maxGauge`, `gainPerHit`, `totalInvulnerable`
  (จำนวนฮิตที่ต้องใช้ = `maxGauge / gainPerHit`)
- `config/chainbreak-client.toml`: ตำแหน่งหลอด, เปิด/ปิดแฟลช, ความแรงแฟลช, เปิด/ปิดกล้องสั่น, ความแรงกล้องสั่น

## Build

ต้องใช้ JDK 17

```
gradle wrapper --gradle-version 8.8
./gradlew build
```

ไฟล์ jar อยู่ที่ `build/libs/chainbreak-1.0.0.jar` → ใส่ในโฟลเดอร์ `mods` พร้อม Epic Fight 20.14.x (1.20.1 Forge)

ถ้า Gradle หา Epic Fight ไม่เจอ ให้เช็กค่า `epicfight_version` ใน `gradle.properties`
(ปัจจุบัน `20.14.17-mc1.20.1-forge` จาก Modrinth maven)
