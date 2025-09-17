package com.example.controlecontas.activity.cartao;

import static com.example.controlecontas.utils.Utils.adicionarMes;
import static com.example.controlecontas.utils.Utils.diminuirMes;
import static com.example.controlecontas.utils.Utils.getNomeAno;
import static com.example.controlecontas.utils.Utils.getNomeMes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controlecontas.R;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.despesa.Despesa;
import com.example.controlecontas.database.despesa.DespesaDao;
import com.example.controlecontas.hashmap.DespesasPorCategoria;
import com.example.controlecontas.hashmap.GraficoDespesas;
import com.github.mikephil.charting.data.PieEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumoCartaoActivity extends AppCompatActivity {

    private ListView listViewResumoCartao;

    private TextView textTotalGeral;
    private TextView textResumoTotalCartao;
    private DespesaDao dao;
    private EditText dataAtual;
    private EditText dataFinal;
    private Button proximaData;
    private Button dataAnterior;
    private List<Despesa> listaDespesas;
    private TextView textResumo;
    private TextView btnVoltar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_cartao);
        AppDatabase db = AppDatabase.getDatabase(this);
        dao = db.despesaDao();
        btnVoltar = findViewById(R.id.btnVoltar);
        listViewResumoCartao = findViewById(R.id.listViewResumoCartao);
        textTotalGeral = findViewById(R.id.textTotalGeral);
        textResumo = findViewById(R.id.textResumo);
        proximaData = findViewById(R.id.proximo);
        dataAnterior = findViewById(R.id.anterior);
        dataAtual = findViewById(R.id.dataAtualCartao);
        dataFinal = findViewById(R.id.dataFinalCartao);
        textResumoTotalCartao = findViewById(R.id.textResumoTotalCartao);
        setarDataInicialFinalDoMes();
        acaoBotaoVoltar();

        listaDespesas = dao.obterDespesasPorDataECartao(dataAtual.getText().toString(), dataFinal.getText().toString());

        acaoProximoMes();
        acaoMesAnterior();
        exibirTotalCartao();
        exibirListaDespesaPorCategoria();
        DespesasPorCategoria result = mapearDespesasPorCategoria(listaDespesas);
        exibirTotalMes(listaDespesas);
        GraficoDespesas despesasPorCategoria = mapearListaEGraficoDeDespesas(result);
        plotarGraficoDespesasPorCategoria(despesasPorCategoria);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void acaoProximoMes() {
        proximaData.setOnClickListener(v -> {
            String dataInicio = adicionarMes(dataAtual.getText().toString(), 1);
            String dataFim = adicionarMes(dataFinal.getText().toString(), 1);
            dataAtual.setText(dataInicio);
            dataFinal.setText(dataFim);
            atualizarTituloResumo();
            detalhesFiltroPorData(dataInicio, dataFim);
            exibirTotalCartao();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void acaoMesAnterior() {
        dataAnterior.setOnClickListener(v -> {
            String dataInicio = diminuirMes(dataAtual.getText().toString(), 1);
            String dataFim = diminuirMes(dataFinal.getText().toString(), 1);
            dataAtual.setText(dataInicio);
            dataFinal.setText(dataFim);
            atualizarTituloResumo();
            detalhesFiltroPorData(dataInicio, dataFim);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setarDataInicialFinalDoMes() {
        LocalDate primeiroDia = LocalDate.now().withDayOfMonth(1);
        LocalDate ultimoDia = primeiroDia.withDayOfMonth(primeiroDia.lengthOfMonth());

        dataAtual.setText(primeiroDia.toString());
        dataFinal.setText(ultimoDia.toString());
        atualizarTituloResumo();
    }

    private void acaoBotaoVoltar() {
        btnVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void atualizarTituloResumo() {
        System.out.println("DATA: " + dataAtual.getText());
        String nomeMes = getNomeMes(dataAtual.getText().toString());
        String ano = getNomeAno(dataAtual.getText().toString());

        String tituloResumo = "Fatura de " + nomeMes + " de " + ano;

        textResumo.setText(tituloResumo);
    }

    @NonNull
    private static GraficoDespesas mapearListaEGraficoDeDespesas(DespesasPorCategoria result) {
        ArrayList<String> resumoLista = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : result.totaisPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double valor = entry.getValue();
            resumoLista.add(result.categoriasEmoji.get(categoria) + " " + categoria + ": R$ " + String.format("%.2f", valor));
            pieEntries.add(new PieEntry((float) valor, categoria));
        }
        GraficoDespesas despesasPorCategoria = new GraficoDespesas(resumoLista, pieEntries);
        return despesasPorCategoria;
    }

    @NonNull
    private static DespesasPorCategoria mapearDespesasPorCategoria(List<Despesa> listaDespesas) {
        HashMap<String, Double> totaisPorCategoria = new HashMap<>();
        HashMap<String, String> categoriasEmoji = new HashMap<>();

        if (listaDespesas != null) {
            for (Despesa despesa : listaDespesas) {
                categoriasEmoji.put(despesa.getCategoria(), despesa.getEmoji());
                if (totaisPorCategoria.containsKey(despesa.getCategoria())) {
                    totaisPorCategoria.put(despesa.getCategoria(), totaisPorCategoria.get(despesa.getCategoria()) + despesa.getValor());
                } else {
                    totaisPorCategoria.put(despesa.getCategoria(), despesa.getValor());
                }
            }
        }
        return new DespesasPorCategoria(totaisPorCategoria, categoriasEmoji);
    }

    private void exibirTotalMes(List<Despesa> listaDespesas) {
        double totalGeral = 0.0;
        for (Despesa despesa : listaDespesas) {
            totalGeral += despesa.getValor();
        }
        textTotalGeral.setText("💰 Total fatura: R$ " + String.format("%.2f", totalGeral));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exibirTotalCartao() {
        double totalGeral = dao.getFaturaCompletaCartao(dataAtual.getText().toString());

        textResumoTotalCartao.setText("💰 Saldo utilizado: R$ " + String.format("%.2f", totalGeral));
    }

    private void exibirListaDespesaPorCategoria() {
        listViewResumoCartao.setOnItemClickListener((parent, view, position, id) -> {
            String itemClicado = (String) parent.getItemAtPosition(position);
            String semEmoji = itemClicado.substring(2); // Remove o primeiro caractere (emoji)
            String categoria = semEmoji.split(":")[0].trim(); // Pega tudo até o primeiro ":" e remove espaços

            String dataInicio = dataAtual.getText().toString();
            String dataFim = dataFinal.getText().toString();

            Intent intent = new Intent(ResumoCartaoActivity.this, DetalhesCartaoActivity.class);
            intent.putExtra("categoria", categoria);
            intent.putExtra("data_inicio", dataInicio);
            intent.putExtra("data_fim", dataFim);
            startActivity(intent);
        });
    }

    private void plotarGraficoDespesasPorCategoria(GraficoDespesas despesasPorCategoria) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_categoria_resumo, R.id.textCategoriaValor, despesasPorCategoria.resumoLista);
        listViewResumoCartao.setAdapter(adapter);
        listaDespesas.forEach(d -> System.out.println("DESPESA: " + d.toString()));
    }

    private void detalhesFiltroPorData(String dataInicio, String dataFim) {
        listaDespesas = dao.obterDespesasPorDataECartao(dataInicio, dataFim);

        listaDespesas.forEach(d -> System.out.println("DESPESA: " + d.toString()));
        exibirListaDespesaPorCategoria();
        DespesasPorCategoria result = mapearDespesasPorCategoria(listaDespesas);
        exibirTotalMes(listaDespesas);
        GraficoDespesas despesasPorCategoria = mapearListaEGraficoDeDespesas(result);
        plotarGraficoDespesasPorCategoria(despesasPorCategoria);
    }
}