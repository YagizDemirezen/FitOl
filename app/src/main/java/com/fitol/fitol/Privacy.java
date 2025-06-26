package com.fitol.fitol;
import com.fitol.FitOl.R;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Privacy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.textView1);
        String gizlilikPolitikasi = "<b>Son Güncelleme Tarihi: 17.03.2025 </b><br><br>" +
                "FitOl ekibi olarak kullanıcı gizliliğini önemsiyoruz. Bu gizlilik politikası, uygulamamızın nasıl veri topladığını, işlediğini, sakladığını ve koruduğunu açıklar.<br><br>" +
                "<b>1. Toplanan Bilgiler</b><br>" +
                "FitOl, kullanıcı deneyimini iyileştirmek için aşağıdaki bilgileri toplayabilir:<br>" +
                "- <b>Hesap Bilgileri:</b> Ad, soyad, e-posta adresi<br>" +
                "- <b>Sağlık ve Aktivite Verileri:</b> Kullanıcının girdiği kilo, yaş, boy ve günlük aktivite bilgileri<br>" +
                "- <b>Cihaz Bilgileri:</b> IP adresi, cihaz modeli, işletim sistemi versiyonu<br>" +
                "- <b>Kullanım Verileri:</b> Uygulama içi etkileşimler ve kullanım alışkanlıkları<br><br>" +
                "<b>2. Verilerin Kullanımı</b><br>" +
                "Toplanan veriler aşağıdaki amaçlarla kullanılabilir:<br>" +
                "- Uygulamanın kişiselleştirilmesi ve kullanıcı deneyiminin geliştirilmesi<br>" +
                "- Teknik destek sağlamak ve hizmetleri iyileştirmek<br>" +
                "- Güvenlik ve dolandırıcılıkla mücadele önlemlerinin alınması<br>" +
                "- Yasal gereklilikleri yerine getirmek<br><br>" +
                "<b>3. Verilerin Paylaşımı</b><br>" +
                "FitOl, kullanıcı verilerini üçüncü taraflarla paylaşmaz. Ancak, aşağıdaki durumlarda paylaşım yapılabilir:<br>" +
                "- Yasal mercilerden gelen zorunlu talepler doğrultusunda<br>" +
                "- Hizmet sağlayıcılarla, sadece anonim ve istatistiksel veriler kullanılarak<br><br>" +
                "<b>4. Verilerin Saklanması ve Güvenliği</b><br>" +
                "Kullanıcı verileri güvenli sunucularda saklanır ve yetkisiz erişime karşı koruma sağlanır. FitOl, en güncel güvenlik önlemlerini uygular.<br><br>" +
                "<b>5. Kullanıcı Hakları</b><br>" +
                "Kullanıcılar aşağıdaki haklara sahiptir:<br>" +
                "- Kişisel verilerini görüntüleme, güncelleme veya silme hakkı<br>" +
                "- Veri işleme sürecine itiraz etme hakkı<br>" +
                "- Hesaplarını ve verilerini tamamen silme talebinde bulunma hakkı<br><br>" +
                "<b>6. Politika Güncellemeleri</b><br>" +
                "FitOl, bu gizlilik politikasını zaman zaman güncelleyebilir. Güncellenmiş versiyon uygulama içinde yayımlandığında yürürlüğe girer.<br><br>" +
                "<b>7. İletişim</b><br>" +
                "Gizlilik politikamız hakkında sorularınız için fitol.1997@gmail.com adresinden bizimle iletişime geçebilirsiniz.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(gizlilikPolitikasi, Html.FROM_HTML_MODE_LEGACY));
        }
    }
}