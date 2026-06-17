import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Scanner;

public class MaquinaVirtual {

    private List<String> instrucoes;
    private Stack<Integer> pilha = new Stack<>();
    private Map<String, Integer> memoria = new HashMap<>();

    public MaquinaVirtual(List<String> instrucoes) {
        this.instrucoes = instrucoes;
    }

    public void executar() {
        int linhaAtual = 0; // ponteiro que diz qual instrução esta lendo

        while (linhaAtual < instrucoes.size()) {
            String linha = instrucoes.get(linhaAtual);
            String[] partes = linha.split(" "); // separa o comando do numero/variavel
            String comando = partes[0];

            switch (comando) {
                case "PUSH":
                    pilha.push(Integer.parseInt(partes[1]));
                    linhaAtual++;
                    break;

                case "ADD":
                    int dirAdd = pilha.pop();
                    int esqAdd = pilha.pop();
                    pilha.push(esqAdd + dirAdd);
                    linhaAtual++;
                    break;

                case "SUB":
                    int dirSub = pilha.pop();
                    int esqSub = pilha.pop();
                    pilha.push(esqSub - dirSub);
                    linhaAtual++;
                    break;

                case "MUL":
                    int dirMul = pilha.pop();
                    int esqMul = pilha.pop();
                    pilha.push(esqMul * dirMul);
                    linhaAtual++;
                    break;

                case "DIV":
                    int dirDiv = pilha.pop();
                    int esqDiv = pilha.pop();
                    pilha.push(esqDiv / dirDiv);
                    linhaAtual++;
                    break;

                case "STORE":
                    memoria.put(partes[1], pilha.pop()); // tira da pilha e salva na memoria
                    linhaAtual++;
                    break;

                case "LOAD":
                    pilha.push(memoria.get(partes[1])); // pega da memoria e joga na pilha
                    linhaAtual++;
                    break;

                case "READ":
                    Scanner teclado = new Scanner(System.in);
                    System.out.print("> ");
                    pilha.push(teclado.nextInt()); // Lê o número e joga no topo da pilha
                    linhaAtual++;
                    break;

                case "PRINT":
                    System.out.println(pilha.pop());
                    linhaAtual++;
                    break;

                // COMPARAÇÕES
                case "CMP_MAIOR":
                    int dirMaior = pilha.pop();
                    int esqMaior = pilha.pop();
                    pilha.push(esqMaior > dirMaior ? 1 : 0);
                    linhaAtual++;
                    break;

                case "CMP_MENOR":
                    int dirMenor = pilha.pop();
                    int esqMenor = pilha.pop();
                    pilha.push(esqMenor < dirMenor ? 1 : 0);
                    linhaAtual++;
                    break;

                case "CMP_IGUAL":
                    int dirIgual = pilha.pop();
                    int esqIgual = pilha.pop();
                    pilha.push(esqIgual == dirIgual ? 1 : 0);
                    linhaAtual++;
                    break;

                case "CMP_DIF":
                    int dirDif = pilha.pop();
                    int esqDif = pilha.pop();
                    pilha.push(esqDif != dirDif ? 1 : 0);
                    linhaAtual++;
                    break;

                // CONTROLE DE FLUXO
                case "JMP":
                    // pula pra linha indicada
                    linhaAtual = Integer.parseInt(partes[1]);
                    break;

                case "JMPF":
                    int condicao = pilha.pop();
                    if (condicao == 0) { // Pula se o topo da pilha for falso
                        linhaAtual = Integer.parseInt(partes[1]);
                    } else {
                        linhaAtual++; // se for verdadeiro segue
                    }
                    break;

                default:
                    throw new RuntimeException("Instrução desconhecida: " + comando);
            }
        }
    }
}