package com.fitol.fitol;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fitol.FitOl.R;

public class Terms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView sartlarText = findViewById(R.id.textView11);
        String sartlarVeKosullar = " <b>1. Genel Şartlar</b><br>" +
                "FitOl uygulamasını kullanarak aşağıda belirtilen şartları ve koşulları kabul etmiş olursunuz. " +
                "Bu şartlar zaman zaman güncellenebilir ve güncellenmiş şartlar uygulama üzerinden erişilebilir olacaktır.<br><br>" +

                "<b>2. Hizmet Tanımı</b><br>" +
                "FitOl, kullanıcılara sağlıklı yaşam, diyet takibi ve kilo yönetimi konusunda destek sağlayan bir mobil uygulamadır. " +
                "Uygulama, verilen bilgileri kişisel bir sağlık önerisi olarak değil, yalnızca rehberlik amacıyla sunmaktadır. " +
                "Diyet planları ve öneriler, kullanıcıların genel sağlık bilgileri temel alınarak hazırlanır ancak kişisel sağlık durumunuza özel değildir. " +
                "Bu nedenle, FitOl’u kullanmadan önce bir sağlık uzmanına danışmanız önerilir.<br><br>" +

                "<b>3. Kullanıcı Sorumlulukları</b><br>" +
                "Kullanıcılar, FitOl’a sağladıkları bilgilerin doğruluğundan ve güncelliğinden sorumludur.<br>" +
                "Uygulama üzerinden sunulan diyet planları ve öneriler bir sağlık uzmanının yerini tutmaz. " +
                "Sağlıkla ilgili konularda uzman bir doktora danışmanız önerilir.<br>" +
                "FitOl'ü sadece kişisel kullanım amacıyla kullanabilirsiniz. Ticari kullanım yasaktır.<br>" +
                "Kullanıcılar, uygulama içerisindeki bilgileri kötüye kullanmamayı ve yanıltıcı bilgiler paylaşmamayı kabul eder.<br><br>" +

                "<b>4. Veri Gizliliği ve Güvenliği</b><br>" +
                "FitOl, kullanıcı verilerini gizli tutar ve üçüncü taraflarla paylaşmaz. " +
                "Ancak, hizmet kalitesini artırmak amacıyla anonim veriler analiz edilebilir. " +
                "Detaylı bilgi için Gizlilik Politikamızı inceleyebilirsiniz.<br>" +
                "Kullanıcılar, hesaplarının güvenliğinden sorumludur ve üçüncü şahıslarla giriş bilgilerini paylaşmamaları önerilir.<br><br>" +

                "<b>5. Uygulama Kullanım Kısıtlamaları</b><br>" +
                "Uygulama kötüye kullanım, hile veya yetkisiz erişim durumlarında kullanıcının erişimini sınırlama hakkına sahiptir.<br>" +
                "Kullanıcılar, uygulamayı yasalara ve genel ahlak kurallarına uygun şekilde kullanmayı kabul eder.<br>" +
                "FitOl, kullanıcının hesabını askıya alma veya silme hakkını saklı tutar.<br><br>" +

                "<b>6. Sorumluluk Reddi</b><br>" +
                "FitOl, uygulamada sunulan bilgilerin doğruluğunu garanti etmez ve doğabilecek sağlık sorunlarından sorumlu değildir.<br>" +
                "Kullanıcılar, uygulamayı kendi sorumlulukları dahilinde kullanır.<br>" +
                "Uygulama, üçüncü taraf kaynaklardan alınan bilgileri içerebilir ve FitOl, bu bilgilerin doğruluğunu garanti edemez.<br><br>" +

                "<b>7. Şartların Güncellenmesi</b><br>" +
                "FitOl, şartlarını ve koşullarını güncelleme hakkını saklı tutar. " +
                "Güncellenmiş şartlar uygulama içinde yayımlandığında geçerli olacaktır. " +
                "Bu nedenle, kullanıcılar belirli aralıklarla şartları gözden geçirmelidir.<br><br>" +

                "<b>8. İletişim</b><br>" +
                "Şartlar ve koşullarla ilgili sorularınız için<br> fitol.1997@gmail.com adresininden bizimle iletişime geçebilirsiniz.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sartlarText.setText(Html.fromHtml(sartlarVeKosullar, Html.FROM_HTML_MODE_LEGACY));
        }

    }
}