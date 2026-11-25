# Mesai Takip 📱📊

**Mesai Takip**, kişisel mesai saatlerinizi, vardiyalarınızı, ek ödemelerinizi ve maaş hesaplamalarınızı kolayca takip etmenizi sağlayan modern bir mobil uygulamadır.

---

## ✨ Özellikler

- 📊 **Aylık Maaş & Mesai Hesabı**: Temel maaş, fazla mesai katsayısı, pazar mesaisi, resmi tatil ücreti, gece vardiyası primi, yemek/yol yardımı ve avans/kesinti hesaplamaları.
- 📄 **Detaylı Aylık PDF Raporu**: Tüm ayın her bir gününü kapsayan (giriş-çıkış saatleri, molalar, fazla mesai, renkli durum tag'leri ve notlar) profesyonel PDF raporu oluşturma ve paylaşma.
- 🌙 **Açık & Karanlık Tema (Dark Mode)**: Göz yormayan modern Material 3 arayüzü, Sistem Varsayılanı, Açık veya Karanlık tema seçenekleri.
- 📅 **İnteraktif Takvim**: Günlük çalışma saatleri, mola süreleri ve vardiya türlerini tek tıkla girme.
- 📈 **İstatistikler & Geçmiş**: Haftalık çalışma saati grafiği, son 12 ayın maaş geçmişi ve genel özetler.
- ☁️ **Veri Yedekleme ve Geri Yükleme**: Verilerinizi ve maaş ayarlarınızı JSON formatında dışa aktarma, paylaşma ve dosyadan geri yükleme.
- 🔐 **Gizlilik ve Güvenlik**: Tüm veriler cihazınızda yerel SQLite (Room) veritabanında güvenle saklanır.

---

## 🛠️ Kullanılan Teknolojiler & Mimari

- **Dil**: Kotlin
- **Arayüz**: Jetpack Compose (Material 3)
- **Mimari**: MVVM + Clean Architecture (Domain, Data, Presentation)
- **Bağımlılık Enjeksiyonu**: Hilt / Dagger
- **Veritabanı**: Room Database (SQLite)
- **Asenkron Programlama**: Coroutines & StateFlow
- **Raporlama**: Native `PdfDocument` & `FileProvider`
- **Navigasyon**: Jetpack Navigation Compose

---

## 📋 Tamamlanan Özellikler (Done)

- [x] **Aylık Maaş ve Mesai Hesaplama Motoru** (Otomatik/manuel saatlik ücret, katsayılar, prim, yol/yemek, avans/kesinti)
- [x] **Aylık PDF Raporu Dışa Aktarma** (Tüm ayın her bir gününü içeren döküm, durum etiketleri ve doğrudan paylaşım)
- [x] **Açık & Karanlık Tema (Dark Mode)** (Sistem Varsayılanı, Açık ve Karanlık tema seçimi ve veritabanı kalıcılığı)
- [x] **Veri Yedekleme ve Geri Yükleme** (JSON formatında tüm kayıtları dışa aktarma, paylaşma ve dosyadan geri yükleme)
- [x] **Gece Vardiyası ve Gece Yarısını Aşan Süre Hesabı** (Overnight shifts için doğru çalışma süresi hesabı)
- [x] **Ondalık Sayı ve Klavye Uyum Özelliği** (Virgüllü/noktalı girişlerin düzgün işlenmesi)
- [x] **İnteraktif Takvim ve Gün Detay Alanı** (Hızlı normal gün doldurma, çalışma saatleri, mola ve not alma)
- [x] **İstatistikler ve Son 12 Ay Geçmişi** (Grafiksel maaş analizi ve haftalık çalışma süreleri)
- [x] **Room Veritabanı ve Şema Migrasyonu** (`MIGRATION_1_2` ile güvenli veritabanı sürüm güncellemeleri)

---

## 🗺️ Yol Haritası ve Gelecek Planları (Roadmap / TODO)

- [ ] 🔔 **Günlük ve Aylık Bildirim Hatırlatıcıları** (Günün sonunda mesai kaydı girme ve maaş günü hatırlatıcısı)
- [ ] 🔒 **Uygulama Kilit Güvenliği** (Biyometrik Parmak İzi / Yüz Tanıma ve 4 haneli PIN kodu koruması)
- [ ] 🏢 **Çoklu İş Yeri ve Vardiya Şablonları** (Birden fazla iş yeri tanımı ve hızlı vardiya saatleri seçimi)
- [ ] 🌍 **Çoklu Dil Desteği** (İngilizce ve Almanca dil seçenekleri)
- [ ] 🍎 **iOS Sürümü (Kotlin Multiplatform / Swift)** (İleriki tarihlerde iPhone ve iPad desteği)

---

## 🚀 Projeyi Çalıştırma

1. Repoyu klonlayın:
   ```bash
   git clone https://github.com/Rexi489/Mesai-Takip.git
   ```
2. Android Studio (Ladybug veya üstü) ile projeyi açın.
3. Gradle senkronizasyonunun tamamlanmasını bekleyin.
4. Android 8.0 (API level 26) veya üzeri bir cihaz/emülatörde projeyi çalıştırın (`Run 'app'`).

---

## 📜 Lisans

Bu proje açık kaynaklıdır ve MIT şartlarına tabidir.
