package com.example.controlecontas.activity.devedores;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controlecontas.R;
import com.example.controlecontas.adapter.DevedoresAdapter;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.devedores.Devedor;
import com.example.controlecontas.database.devedores.DevedoresDao;

import java.util.List;

public class DetalhesDevedoresActivity extends AppCompatActivity {

    private TextView textTotalPagamentos, tabNaoPagos, tabPagos;
    private ListView listViewDetalhesPagamentos;
    private DevedoresDao dao;
    private TextView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_devedores);
        AppDatabase db = AppDatabase.getDatabase(this);
        dao = db.devedoresDao();

        listViewDetalhesPagamentos = findViewById(R.id.listViewDetalhesPagamentos);
        textTotalPagamentos = findViewById(R.id.textTotalPagamentos);
        btnVoltar = findViewById(R.id.btnVoltar);
        tabPagos = findViewById(R.id.tabPagos);
        tabNaoPagos = findViewById(R.id.tabNaoPagos);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });
        carregarDevedores();

        carregarDevedoresNaoPagos();
    }

    private void carregarDevedores() {
        tabNaoPagos.setOnClickListener(v -> {
//            tabNaoPagos.setBackgroundColor();
//            tabPagos.setBackgroundColor(R.drawable.tab_background_unselected);
            tabNaoPagos.setTextColor(Color.WHITE);
            tabPagos.setTextColor(Color.GRAY);

            carregarDevedoresNaoPagos();
        });

        tabPagos.setOnClickListener(v -> {
//            tabPagos.setBackgroundResource(R.drawable.tab_background_selected);
//            tabNaoPagos.setBackgroundResource(R.drawable.tab_background_unselected);
            tabPagos.setTextColor(Color.WHITE);
            tabNaoPagos.setTextColor(Color.GRAY);

            carregarDevedoresPagos();
        });

    }

    private void carregarDevedoresPagos() {
        List<Devedor> devedores = dao.obterDevedoresPagos();

        DevedoresAdapter adapter = new DevedoresAdapter(
                this,
                devedores,
                this::carregarDevedoresPagos
        );

        listViewDetalhesPagamentos.setAdapter(adapter);
        calcularTotal(devedores);
    }

    private void carregarDevedoresNaoPagos() {
        List<Devedor> devedores = dao.obterDevedores();

        DevedoresAdapter adapter = new DevedoresAdapter(
                this,
                devedores,
                this::carregarDevedoresNaoPagos
        );

        listViewDetalhesPagamentos.setAdapter(adapter);
        calcularTotal(devedores);
    }

    private void calcularTotal(List<Devedor> devedors) {
        double total = 0.0;
        for (Devedor d : devedors) {
            total += d.getValor();
        }

        String totalFormatado = String.format("%.2f", total).replace(".", ",");
        textTotalPagamentos.setText("💰 Total gasto: R$ " + totalFormatado);
    }
}