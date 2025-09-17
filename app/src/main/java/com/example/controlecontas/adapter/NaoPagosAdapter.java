package com.example.controlecontas.adapter;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
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
import com.example.controlecontas.activity.pendents.PendentesActivity;
import com.example.controlecontas.database.AppDatabase;
import com.example.controlecontas.database.despesa.Despesa;
import com.example.controlecontas.database.despesa.DespesaDao;
import com.example.controlecontas.utils.Utils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class NaoPagosAdapter extends ArrayAdapter<Despesa> {

    private DespesaDao dao;
    private Context context;
    private List<Despesa> despesas;
    private Runnable atualizarCallback;

    public NaoPagosAdapter(Context context, List<Despesa> despesas, Runnable atualizarCallback) {
        super(context, 0, despesas);
        this.context = context;
        this.despesas = despesas;
        this.atualizarCallback = atualizarCallback;
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.despesaDao();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Despesa despesa = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nao_pago, parent, false);
        }

        TextView txtDespesa = convertView.findViewById(R.id.txtDespesaNaoPago);
        TextView txtData = convertView.findViewById(R.id.txtDataNaoPago);
        ImageView btnPagar = convertView.findViewById(R.id.btnPagar);

        String nome = TextUtils.isEmpty(despesa.getNome()) ? despesa.getCategoria() : despesa.getNome();

        NumberFormat formatoBrasileiro = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatoBrasileiro.format(despesa.getValor());

        txtDespesa.setText(despesa.getEmoji() + " " + nome + ": " + valorFormatado);
        txtData.setText("📆 " + Utils.formatarDataParaExibicao(despesa.getDataDespesa()));

        btnPagar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Pagar despesa")
                    .setMessage("Deseja realmente marcar esta despesa como paga?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        Despesa despesaBanco = dao.obterPorId(despesa.getId());
                        despesaBanco.setIsPago("S");
                        dao.atualizarDespesa(despesaBanco);
                        atualizarCallback.run(); //Atualiza a lista LOCAL, acredito que seja na main ou detalhes, ainda nao descobri
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).atualizarListaDespesas();
                        } else if (context instanceof PendentesActivity) {
                            ((PendentesActivity) context).setResult(RESULT_OK);
                        }

                        Toasty.success(context, "Despesa Paga", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return convertView;
    }
}



