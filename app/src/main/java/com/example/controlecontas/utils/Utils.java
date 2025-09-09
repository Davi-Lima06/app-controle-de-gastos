package com.example.controlecontas.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String formatarDataParaExibicao(String dataOriginal) {
        SimpleDateFormat formatoBanco = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoExibicao = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date data = formatoBanco.parse(dataOriginal);
            return formatoExibicao.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
            return dataOriginal;
        }
    }

    public static String[] getPrimeiroEUltimoDiaDoMes(String dataDespesa) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date data = sdf.parse(dataDespesa);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String primeiroDia = sdf.format(calendar.getTime());

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String ultimoDia = sdf.format(calendar.getTime());

            return new String[]{primeiroDia, ultimoDia};

        } catch (ParseException e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String adicionarMes(String dataDespesa, int i) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate data = LocalDate.parse(dataDespesa, formatter);

        LocalDate novaData = data.plusMonths(i);

        return novaData.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String diminuirMes(String dataDespesa, int i) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate data = LocalDate.parse(dataDespesa, formatter);

        LocalDate novaData = data.minusMonths(i);

        return novaData.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getNomeMes(String dataRecebida) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate data = LocalDate.parse(dataRecebida, formatter);
        DateTimeFormatter formatterMes = DateTimeFormatter.ofPattern("MMMM", new Locale("pt", "BR"));
        return data.format(formatterMes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getNomeAno(String dataRecebida) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate data = LocalDate.parse(dataRecebida, formatter);
        DateTimeFormatter formatterAno = DateTimeFormatter.ofPattern("yyyy", new Locale("pt", "BR"));
        return data.format(formatterAno);
    }
}
