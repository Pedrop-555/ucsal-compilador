import java.util.ArrayList;
import java.util.List;

public class GeradorCodigoVisitor extends UcsalBaseVisitor<String> {

    // guarda o bytecode final gerado
    public List<String> linhasDeCodigo = new ArrayList<>();

    @Override
    public String visitCmdImprimir(UcsalParser.CmdImprimirContext ctx) {
        visit(ctx.operacao());
        linhasDeCodigo.add("PRINT");
        return null;
    }

    @Override
    public String visitOpNumero(UcsalParser.OpNumeroContext ctx) {
        String num = ctx.NUMERO().getText();
        linhasDeCodigo.add("PUSH " + num);
        return null;
    }

    @Override
    public String visitCmdDeclInteiro(UcsalParser.CmdDeclInteiroContext ctx) {
        String nomeVar = ctx.ID().getText();
        visit(ctx.operacao()); // resolve o que tá depois do '=' e joga na pilha
        linhasDeCodigo.add("STORE " + nomeVar); // tira da pilha e salva
        return null;
    }

    @Override
    public String visitCmdDeclBooleano(UcsalParser.CmdDeclBooleanoContext ctx) {
        String nomeVar = ctx.ID().getText();
        visit(ctx.operacao());
        linhasDeCodigo.add("STORE " + nomeVar);
        return null;
    }

    @Override
    public String visitCmdAtribuicao(UcsalParser.CmdAtribuicaoContext ctx) {
        String nomeVar = ctx.ID().getText();
        visit(ctx.operacao());
        linhasDeCodigo.add("STORE " + nomeVar);
        return null;
    }

    @Override
    public String visitOpVariavel(UcsalParser.OpVariavelContext ctx) {
        String nomeVar = ctx.ID().getText();
        linhasDeCodigo.add("LOAD " + nomeVar); // pega da memória e joga na pilha
        return null;
    }

    // Aproveitando pra adicionar os booleanos também (1 = verdadeiro, 0 = falso)
    @Override
    public String visitOpVerdadeiro(UcsalParser.OpVerdadeiroContext ctx) {
        linhasDeCodigo.add("PUSH 1");
        return null;
    }

    @Override
    public String visitOpFalso(UcsalParser.OpFalsoContext ctx) {
        linhasDeCodigo.add("PUSH 0");
        return null;
    }

    @Override
    public String visitOpSomaSub(UcsalParser.OpSomaSubContext ctx) {
        visit(ctx.operacao(0));
        visit(ctx.operacao(1));

        if (ctx.SOMA() != null) {
            linhasDeCodigo.add("ADD");
        } else {
            linhasDeCodigo.add("SUB");
        }
        return null;
    }

    @Override
    public String visitOpMultDiv(UcsalParser.OpMultDivContext ctx) {
        visit(ctx.operacao(0));
        visit(ctx.operacao(1));

        if (ctx.MULT() != null) {
            linhasDeCodigo.add("MUL");
        } else {
            linhasDeCodigo.add("DIV");
        }
        return null;
    }

    @Override
    public String visitOpRelacional(UcsalParser.OpRelacionalContext ctx) {
        visit(ctx.operacao(0));
        visit(ctx.operacao(1));

        // A VM vai tirar dois da pilha e empilhar 1 ou 0
        if (ctx.MAIOR() != null) linhasDeCodigo.add("CMP_MAIOR");
        if (ctx.MENOR() != null) linhasDeCodigo.add("CMP_MENOR");
        if (ctx.IGUAL() != null) linhasDeCodigo.add("CMP_IGUAL");
        if (ctx.DIF() != null)   linhasDeCodigo.add("CMP_DIF");

        return null;
    }

    @Override
    public String visitCmdCaso(UcsalParser.CmdCasoContext ctx) {
        visit(ctx.operacao()); // resolve a condicao (deixa 1 ou 0 na pilha)

        // Guarda a posição e adiciona um JMPF provisório
        int posJmpf = linhasDeCodigo.size();
        linhasDeCodigo.add("JMPF ???");

        visit(ctx.bloco(0)); // gera o código do bloco verdadeiro

        if (ctx.bloco().size() > 1) { // se tem o casonao (else)
            // O bloco verdadeiro precisa pular o casonao pra não executar os dois
            int posJmpFim = linhasDeCodigo.size();
            linhasDeCodigo.add("JMP ???");

            // Agora a gente sabe onde o casonao começa! Atualiza aquele primeiro JMPF
            linhasDeCodigo.set(posJmpf, "JMPF " + linhasDeCodigo.size());

            visit(ctx.bloco(1)); // gera o código do casonao

            // Agora sabe onde tudo acaba. Atualiza o JMP do bloco verdadeiro
            linhasDeCodigo.set(posJmpFim, "JMP " + linhasDeCodigo.size());
        } else {
            // se não tem casonao, o JMPF pula direto pro final
            linhasDeCodigo.set(posJmpf, "JMPF " + linhasDeCodigo.size());
        }

        return null;
    }

    @Override
    public String visitCmdEnquanto(UcsalParser.CmdEnquantoContext ctx) {
        // Guarda a linha onde a condição começa (pra poder voltar nela em cada repetição)
        int inicioLaco = linhasDeCodigo.size();

        visit(ctx.operacao()); // resolve a condição

        int posJmpf = linhasDeCodigo.size();
        linhasDeCodigo.add("JMPF ???"); // se for falso, sai do laço

        visit(ctx.bloco()); // gera o código de dentro do enquanto

        // Quando termina o bloco, obriga a voltar pro início do laço
        linhasDeCodigo.add("JMP " + inicioLaco);

        // Agora sabe onde o laço termina. Atualiza a saída do JMPF
        linhasDeCodigo.set(posJmpf, "JMPF " + linhasDeCodigo.size());

        return null;
    }

    @Override
    public String visitOpLer(UcsalParser.OpLerContext ctx) {
        linhasDeCodigo.add("READ");
        return null;
    }
}