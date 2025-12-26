StreamCache: LFU & Tahminsel Önbellek Yönetim Sistemi
Bu proje, kısıtlı bellek (RAM) kaynaklarını en verimli şekilde kullanmak amacıyla geliştirilmiş, LFU (Least Frequently Used) algoritması ve Tür Tabanlı Tahminleme mekanizması üzerine kurulu bir performans optimizasyon aracıdır.
Teknik Özellikler ve AlgoritmalarÖnbellek Stratejisi (LFU): Önbellek dolduğunda, frekansı (izlenme sayısı) en düşük olan veriyi Açgözlü (Greedy) bir yaklaşımla sistemden tahliye eder.Min-Heap Veri Yapısı: Tahliye edilecek verinin $O(1)$ sürede tespiti ve $O(\log N)$ sürede yönetimi için Java PriorityQueue (Min-Heap) yapısı kullanılmıştır.Tahminsel Ön Yükleme (Predictive Pre-fetching): Kullanıcı bir içerik talep ettiğinde, asenkron olarak aynı türdeki popüler içerikler RAM'e hazırlanır.Yaşlandırma (Aging): Eski popüler verilerin önbelleği işgal etmesini (Cache Pollution) önlemek için verilerin popülarite puanları zamanla sönümlenir.
Performans Analizi
Sistem üzerinden alınan canlı veriler, algoritmanın başarısını doğrulamaktadır:

Veritabanı (Disk) Erişim Süresi: ~25.000 µs

Önbellek (RAM) Erişim Süresi: ~5.000 µs

Verimlilik Artışı: Önbellek katmanı sayesinde veri erişim hızı 5 kat artırılarak %80 performans kazanımı sağlanmıştır.
Kurulum ve Çalıştırma
Projeyi klonlayın: git clone https://github.com/ayseakdogann/MovieCahceAppl.git

PostgreSQL veritabanınızı oluşturun.

Güvenlik: Veritabanı şifrenizi application.properties içine yazmak yerine, IDE'nizde veya sisteminizde DB_PASSWORD isimli bir Environment Variable oluşturarak tanımlayın.

Projeyi çalıştırın ve localhost:9090 üzerinden Admin Paneli'ni takip edin.


Gazi Üniversitesi 3. Sınıf Güz Dönemi 
Algoritmalar Dersi Projesi
Ayşe Akdoğan 23181616011
Melih Ali Çağman 23181616766
