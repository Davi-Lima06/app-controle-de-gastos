package com.example.controlecontas.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.controlecontas.R;
import com.example.controlecontas.activity.MainActivity;
import com.example.controlecontas.activity.devedores.DevedorActivity;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.devedores.Devedor;
import com.example.controlecontas.database.devedores.DevedoresDao;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class DevedoresAdapter extends ArrayAdapter<Devedor> {

    private DevedoresDao dao;
    private Context context;
    private List<Devedor> devedors;
    private Runnable atualizarCallback;

    public DevedoresAdapter(Context context, List<Devedor> devedors, Runnable atualizarCallback) {
        super(context, 0, devedors);
        this.context = context;
        this.devedors = devedors;
        this.atualizarCallback = atualizarCallback;
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.devedoresDao();
    }

    @SuppressLint("NewApi")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Devedor devedor = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.coluna_devedor, parent, false);
        }

        TextView nomeDevedor = convertView.findViewById(R.id.txtDevedorNome);
        ImageView btnPagar = convertView.findViewById(R.id.btnSetarComoPago);

        NumberFormat formatoBrasileiro = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatoBrasileiro.format(devedor.getValor());

        nomeDevedor.setText("▼" + " " + devedor.getNomePessoa()  + ": " + valorFormatado);

        if (devedor.getIsPago().equals("S")) {
            btnPagar.setVisibility(View.GONE);
        }

        String title = devedor.getIsPago().equals("S") ?
                "Detalhes do Pagamento" : "Informações adicionais";

        String message = devedor.getIsPago().equals("S") ?
                "Data pagamento: " + devedor.getDataPagamentoEmprestimo() + "\n" + "Detalhes: " + devedor.getMotivo() :
                "Data do empréstimo: " + devedor.getDataEmprestimo() + "\n" + "Detalhes: " + devedor.getMotivo();

        nomeDevedor.setOnClickListener( v -> {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Fechar", null)
                    .show();
        });

        btnPagar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("O caloteiro pagou ?")
                    .setMessage("Deseja realmente marcar esta despesa como paga?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        Devedor devedorBanco = dao.obterPorId(devedor.getId());
                        devedorBanco.setIsPago("S");
                        devedorBanco.setDataPagamentoEmprestimo(LocalDate.now().toString());
                        dao.atualizarDespesa(devedorBanco);
                        atualizarCallback.run(); //Atualiza a lista LOCAL, acredito que seja na main ou detalhes, ainda nao descobri
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).atualizarListaDespesas();
                        } else if (context instanceof DevedorActivity) {
                            ((DevedorActivity) context).setResult(RESULT_OK);
                        }

                        Toasty.success(context, devedorBanco.getNomePessoa() + " Não é mais caloteiro", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return convertView;
    }
}



