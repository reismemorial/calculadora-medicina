package com.calculadora.medicina;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CalculadoraController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/calcular")
    public String calcular(
            @RequestParam int opcao,
            @RequestParam(defaultValue = "0") double pbl,
            @RequestParam(value = "irats", required = false) List<Double> irats,
            @RequestParam(value = "grats", required = false) List<Double> grats,
            @RequestParam(defaultValue = "0") double lmf,
            @RequestParam(defaultValue = "0") double lpi,
            @RequestParam(defaultValue = "0") double teorica,
            Model model) {

        // calcula média iRAT (percorre a lista e ignora vazios)
        double somaIrat = 0; int qtdIrat = 0;
        if (irats != null) {
            for (Double nota : irats) {
                if (nota != null) { somaIrat += nota; qtdIrat++; }
            }
        }
        double mediaIrat = (qtdIrat > 0) ? (somaIrat / qtdIrat) : 0;

        // calcula média gRAT (percorre a lista e ignora vazios)
        double somaGrat = 0; int qtdGrat = 0;
        if (grats != null) {
            for (Double nota : grats) {
                if (nota != null) { somaGrat += nota; qtdGrat++; }
            }
        }
        double mediaGrat = (qtdGrat > 0) ? (somaGrat / qtdGrat) : 0;

        // pesos (Nova Matriz)
        double pesoPbl = (pbl / 10.0) * 1.5;
        double pesoIrat = (mediaIrat / 10.0) * 1.5;
        double pesoGrat = (mediaGrat / 10.0) * 0.5;
        double pesoLmf = (lmf / 10.0) * 1.2;
        double pesoLpi = (lpi / 10.0) * 0.6;

        double somaParcial = pesoPbl + pesoIrat + pesoGrat + pesoLmf + pesoLpi;

        String resultadoTexto = "";
        String statusTexto = "";

        if (opcao == 1) {
            double pesoTeorica = (teorica / 10.0) * 4.7;
            double notaFinal = somaParcial + pesoTeorica;
            resultadoTexto = String.format("Sua Nota Final: %.2f / 10.00", notaFinal);

            if (notaFinal >= 7.0) { statusTexto = " Parabéns! Você está aprovado direto!"; }
            else if (notaFinal >= 4.0) { statusTexto = String.format("️ Você ficou de exame. Faltaram %.2f pontos.", 7.0 - notaFinal); }
            else { statusTexto = String.format(" Reprovado direto. Faltaram %.2f pontos.", 7.0 - notaFinal); }
        } else if (opcao == 2) {
            double faltaParaSete = 7.0 - somaParcial;
            if (faltaParaSete <= 0) { statusTexto = " Passou direto! Mesmo se tirar zero na Teórica, não cai pra menos de 7."; }
            else {
                double notaNecessaria = (faltaParaSete / 4.7) * 10.0;
                resultadoTexto = String.format("Você acumulou %.2f pontos até agora.", somaParcial);
                if (notaNecessaria <= 10.0) { statusTexto = String.format(" Você precisa tirar no mínimo %.2f na Prova Teórica para fechar com 7.0!", notaNecessaria); }
                else { statusTexto = String.format("️ Mesmo tirando 10 na Teórica, você ficará de exame. Média máxima será %.2f.", somaParcial + 4.7); }
            }
        }

        model.addAttribute("resultado", resultadoTexto);
        model.addAttribute("status", statusTexto);
        model.addAttribute("mediaIrat", String.format("%.2f", mediaIrat));
        model.addAttribute("mediaGrat", String.format("%.2f", mediaGrat));

        return "index";
    }
}