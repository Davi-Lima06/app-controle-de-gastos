package com.example.controlecontas.activity;

import static com.example.controlecontas.utils.Utils.adicionarMes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.controlecontas.R;
import com.example.controlecontas.activity.cartao.ResumoCartaoActivity;
import com.example.controlecontas.activity.categoria.DetalhesCategoriaActivity;
import com.example.controlecontas.activity.categoria.ResumoActivity;
import com.example.controlecontas.activity.devedores.DetalhesDevedoresActivity;
import com.example.controlecontas.activity.devedores.DevedorActivity;
import com.example.controlecontas.activity.pendents.PendentesActivity;
import com.example.controlecontas.adapter.DespesaAdapter;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.despesa.Despesa;
import com.example.controlecontas.database.despesa.DespesaDao;
import com.example.controlecontas.enums.TipoPagamentoEnum;
import com.example.controlecontas.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private DespesaDao dao;
    private Spinner spinnerCategoria, spinnerCartao;
    private EditText editValor;
    private EditText editNomeItem;
    private Button btnAdicionar;
    private ListView listViewDespesas;
    private EditText editData;
    private EditText parcelas;
    private List<Despesa> listaDespesas;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean isPagamentoParcelado;

    private List<String> tiposPagamentosParcelados = List.of(
            "FATURA",
            "CRÉDITO");

    private String formaPagamento;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AppDatabase db = AppDatabase.getDatabase(this);
        dao = db.despesaDao();

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editValor = findViewById(R.id.editValor);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        //btnResumo = findViewById(R.id.btnResumo);
        editNomeItem = findViewById(R.id.editNomeItem);
        listViewDespesas = findViewById(R.id.listViewDespesas);
        editData = findViewById(R.id.editData);
        parcelas = findViewById(R.id.parcelas);
        spinnerCartao = findViewById(R.id.spinnerCartao);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ativarNavBar();

        editarData();
        tipoPagamento();
        categoriaProduto();

        this.atualizarListaDespesas();
        btnAdicionar.setOnClickListener(v -> adicionarDespesa());

        editValor.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editValor.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString) / 100.0;
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                        String formatted = format.format(parsed);

                        current = formatted;
                        editValor.setText(formatted);
                        editValor.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        s.clear();
                    }

                    editValor.addTextChangedListener(this);
                }
            }
        });
    }

    private void categoriaProduto() {
        ArrayList<String> categorias = new ArrayList<>(Arrays.asList(
                "▼ Selecione uma Categoria",
                "🏠 Despesas Fixas",
                "🏢 Apartamento",
                "🚗 Carro",
                "⛽ Combustível",
                "🍽️ Comida",
                "📚 Educação",
                "💸 Empréstimos",
                "🏖️ Lazer",
                "🛒 Mercado",
                "🐶 Pets",
                "🎁 Presente",
                "🏥 Saúde",
                "🛍️ Utilitários",
                "\uD83D\uDE8C Transporte"
        ));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(spinnerAdapter);
    }

    private void tipoPagamento() {
        ArrayList<String> pagamentos = new ArrayList<>(Arrays.asList(
                "CRÉDITO",
                "DÉBITO",
                "PIX",
                "FATURA",
                "TIKET / VALE"
        ));

        // Adapter que liga a lista ao Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, // layout padrão
                pagamentos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCartao.setAdapter(adapter);

        // Listener para capturar seleção
        spinnerCartao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pagamento = parent.getItemAtPosition(position).toString();
                formaPagamento = pagamento;
                if (pagamento.equals("CRÉDITO")|| pagamento.equals("FATURA")) {
                    parcelas.setVisibility(View.VISIBLE);
                    isPagamentoParcelado = true;
                } else {
                    parcelas.setVisibility(View.GONE);
                    isPagamentoParcelado = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void editarData() {
        editData.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String dataFormatada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        editData.setText(dataFormatada);
                    },
                    ano, mes, dia
            );
            datePicker.show();
        });
    }

    private void ativarNavBar() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_resumo) {
                // Era o btnResumo
                Intent intent = new Intent(MainActivity.this, ResumoActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_resumo_cartao) {
                // Era o btnResumoCartao
                Intent intent = new Intent(MainActivity.this, ResumoCartaoActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_resumo_pendentes) {
                Intent intent = new Intent(MainActivity.this, PendentesActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_cadastro_golpistas) {
                Intent intent = new Intent(MainActivity.this, DevedorActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_resumo_devedores) {
                Intent intent = new Intent(MainActivity.this, DetalhesDevedoresActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers(); // fecha o menu depois do clique
            return true;
        });
    }

    public void atualizarListaDespesas() {
        listaDespesas = dao.obterDespesasAdicionadasRecentemente();
        DespesaAdapter adapter = new DespesaAdapter(this, listaDespesas, this::atualizarListaDespesas);
        listViewDespesas.setAdapter(adapter);

        listViewDespesas.setOnItemClickListener((parent, view, position, id) -> {
            Despesa despesaSelecionada = listaDespesas.get(position);
            String categoriaSelecionada = despesaSelecionada.getCategoria();

            Intent intent = new Intent(MainActivity.this, DetalhesCategoriaActivity.class);

            String[] dataInicioEDataFim = Utils.getPrimeiroEUltimoDiaDoMes(despesaSelecionada.getDataDespesa());

            intent.putExtra("categoria", categoriaSelecionada);
            intent.putExtra("data_inicio", dataInicioEDataFim[0]);
            intent.putExtra("data_fim", dataInicioEDataFim[1]);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            atualizarListaDespesas();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void adicionarDespesa() {
        String nomeItem = editNomeItem.getText().toString().trim();
        String categoriaSelecionada = spinnerCategoria.getSelectedItem().toString();
        String valorTexto = editValor.getText().toString().trim();
        String dataDespesa = editData.getText().toString().trim();
        String numeroParcelas = parcelas.getText().toString().trim();
        int totalParcelas = 1;
        String isPago = formaPagamento.equals("FATURA") ? "N" : "S";


        if (categoriaSelecionada.equals("▼ Selecione uma Categoria")) {
            Toasty.warning(this, "Por favor, selecione uma categoria", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            Toasty.warning(this, "Digite o valor da despesa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataDespesa.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dataDespesa = LocalDate.now().format(formatter);
        }

        if (isPagamentoParcelado) {
            if (numeroParcelas.isEmpty()) {
                Toasty.warning(this, "Digite a quantidade de parcelas", Toast.LENGTH_SHORT).show();
            } else {
                totalParcelas = Integer.parseInt(numeroParcelas);
            }
        }

        String cleanString = valorTexto.replaceAll("[^0-9,.]", "");
        cleanString = cleanString.replace(",", ".");
        cleanString = cleanString.replaceAll("\\.(?=.*\\.)", "");
        double valor;

        try {
            valor = Double.parseDouble(cleanString);
        } catch (NumberFormatException e) {
            Toasty.error(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoria = categoriaSelecionada.substring(categoriaSelecionada.offsetByCodePoints(0, 1)).trim();
        String emoji = categoriaSelecionada.substring(0, categoriaSelecionada.offsetByCodePoints(0, 1));
        if (totalParcelas > 1) {
            for (int i = 0; i < totalParcelas; i++) {
                String dataAtualizada = adicionarMes(dataDespesa, i);
                Despesa despesa = new Despesa(nomeItem, categoria, valor, dataAtualizada,emoji, TipoPagamentoEnum.getCodidoPorDescricao(formaPagamento), totalParcelas, isPago);
                dao.inserirDespesa(despesa);
            }
        } else {
            Despesa despesa = new Despesa(nomeItem, categoria, valor, dataDespesa,emoji, TipoPagamentoEnum.getCodidoPorDescricao(formaPagamento), totalParcelas, isPago);
            dao.inserirDespesa(despesa);
        }

        atualizarListaDespesas();

        editNomeItem.setText("");
        editValor.setText("");
        spinnerCategoria.setSelection(0);
        editData.setText("");
        parcelas.setText("1");

        Toasty.success(this, "Despesa adicionada com sucesso!", Toast.LENGTH_SHORT).show();
    }
}