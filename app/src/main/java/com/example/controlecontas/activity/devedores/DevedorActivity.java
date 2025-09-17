package com.example.controlecontas.activity.devedores;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controlecontas.R;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.despesa.DespesaDao;
import com.example.controlecontas.database.devedores.Devedor;
import com.example.controlecontas.database.devedores.DevedoresDao;

import java.text.NumberFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class DevedorActivity extends AppCompatActivity {

    private DevedoresDao dao;

    private EditText editNomeVagabundo, editValorPego, editMotivo;
    private Button btnAdicionarPobre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devedor);
        AppDatabase db = AppDatabase.getDatabase(this);
        dao = db.devedoresDao();

        editMotivo = findViewById(R.id.editMotivo);
        editValorPego = findViewById(R.id.editValorPego);
        editNomeVagabundo = findViewById(R.id.editNomeVagabundo);
        btnAdicionarPobre = findViewById(R.id.btnAdicionarPobre);

        editarValor();
        btnAdicionarPobre.setOnClickListener(v -> cadastrarDevedor());
    }

    private void cadastrarDevedor() {
        String nome = editNomeVagabundo.getText().toString().trim();
        String motivo = editMotivo.getText().toString();
        String valorTexto = editValorPego.getText().toString().trim();

        if (nome.isEmpty()) {
            Toasty.warning(this, "Informe o nome.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            Toasty.warning(this, "Informe o valor", Toast.LENGTH_SHORT).show();
            return;
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

        Devedor devedor = new Devedor(motivo, valor, nome);
        dao.inserirDevedor(devedor);
        editNomeVagabundo.setText("");
        editValorPego.setText("");
        editMotivo.setText("");

        Toasty.success(this, "Vagabundo marcado!", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void editarValor() {
        editValorPego.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editValorPego.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString) / 100.0;
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                        String formatted = format.format(parsed);

                        current = formatted;
                        editValorPego.setText(formatted);
                        editValorPego.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        s.clear();
                    }

                    editValorPego.addTextChangedListener(this);
                }
            }
        });
    }
}