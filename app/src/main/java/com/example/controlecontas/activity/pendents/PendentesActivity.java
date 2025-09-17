package com.example.controlecontas.activity.pendents;

import static com.example.controlecontas.utils.Utils.adicionarMes;
import static com.example.controlecontas.utils.Utils.diminuirMes;
import static com.example.controlecontas.utils.Utils.getNomeAno;
import static com.example.controlecontas.utils.Utils.getNomeMes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controlecontas.R;
import com.example.controlecontas.adapter.NaoPagosAdapter;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.despesa.Despesa;
import com.example.controlecontas.database.despesa.DespesaDao;

import java.time.LocalDate;
import java.util.List;

public class PendentesActivity extends AppCompatActivity {

    private TextView textPagamentosEmAberto;
    private ListView listViewDetalhesEmAberto;
    private TextView textTotalEmAberto;
    private DespesaDao dao;
    private String dataInicio;
    private String dataFim, dataAtual, dataFinal;
    private TextView btnVoltar;
    private Button dataAnterior, proximaData;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendentes);
        AppDatabase db = AppDatabase.getDatabase(this);
        dao = db.despesaDao();
        textPagamentosEmAberto = findViewById(R.id.textPagamentosEmAberto);
        listViewDetalhesEmAberto = findViewById(R.id.listViewDetalhesEmAberto);
        textTotalEmAberto = findViewById(R.id.textTotalEmAberto);
        btnVoltar = findViewById(R.id.btnVoltar);
        proximaData = findViewById(R.id.proximo);
        dataAnterior = findViewById(R.id.anterior);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        setarDataInicialFinalDoMes();
        carregarDespesas();
        acaoMesAnterior();
        acaoProximoMes();
    }

    private void carregarDespesas() {
        List<Despesa> despesas = dao.obterDespesasPendentesDoMes(dataAtual, dataFinal);

        NaoPagosAdapter adapter = new NaoPagosAdapter(
                this,
                despesas,
                this::carregarDespesas
        );

        listViewDetalhesEmAberto.setAdapter(adapter);
        calcularTotal(despesas);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setarDataInicialFinalDoMes() {
        LocalDate primeiroDia = LocalDate.now().withDayOfMonth(1);
        LocalDate ultimoDia = primeiroDia.withDayOfMonth(primeiroDia.lengthOfMonth());

        dataAtual = primeiroDia.toString();
        dataFinal = ultimoDia.toString();
    }
    private void calcularTotal(List<Despesa> despesas) {
        double total = 0.0;
        for (Despesa d : despesas) {
            total += d.getValor();
        }

        String totalFormatado = String.format("%.2f", total).replace(".", ",");
        textTotalEmAberto.setText("💰 Total gasto: R$ " + totalFormatado);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void acaoProximoMes() {
        proximaData.setOnClickListener(v -> {
            String dataInicioMes = adicionarMes(dataAtual, 1);
            String dataFimMes = adicionarMes(dataFinal, 1);
            dataAtual = dataInicioMes;
            dataFinal = dataFimMes;
            carregarDespesas();
            atualizarTituloResumo();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void acaoMesAnterior() {
        dataAnterior.setOnClickListener(v -> {
            String dataInicioMes = diminuirMes(dataAtual, 1);
            String dataFimMes = diminuirMes(dataFinal, 1);
            dataAtual = dataInicioMes;
            dataFinal = dataFimMes;
            carregarDespesas();
            atualizarTituloResumo();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void atualizarTituloResumo() {

        String nomeMes = getNomeMes(dataAtual);
        String ano = getNomeAno(dataAtual);

        String tituloResumo = "Faturas em aberto de " + nomeMes + " de " + ano;

        textPagamentosEmAberto.setText(tituloResumo);
    }
}