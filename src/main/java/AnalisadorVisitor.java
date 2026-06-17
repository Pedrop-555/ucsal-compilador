import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AnalisadorVisitor extends UcsalBaseVisitor<Object> {

    private Map<String, Object> tabelaSimbolos = new HashMap<>();
    private Scanner teclado = new Scanner(System.in);

    @Override
    public Object visitCmdImprimir(UcsalParser.CmdImprimirContext ctx) {
        // Resolve o que tá dentro do parenteses e printa
        Object valor = visit(ctx.operacao());
        System.out.println(valor);
        return null;
    }

    @Override
    public Object visitCmdDeclInteiro(UcsalParser.CmdDeclInteiroContext ctx) {
        String nomeVar = ctx.ID().getText();
        if (tabelaSimbolos.containsKey(nomeVar)) {
            throw new RuntimeException("Erro: Variável '" + nomeVar + "' já declarada.");
        }

        Object valor = visit(ctx.operacao());
        if (!(valor instanceof Integer)) { // Type checking
            throw new RuntimeException("Erro Semântico: A variável '" + nomeVar + "' deve receber um inteiro.");
        }

        tabelaSimbolos.put(nomeVar, valor);
        return null;
    }

    @Override
    public Object visitCmdDeclBooleano(UcsalParser.CmdDeclBooleanoContext ctx) {
        String nomeVar = ctx.ID().getText();
        if (tabelaSimbolos.containsKey(nomeVar)) {
            throw new RuntimeException("Erro: Variável '" + nomeVar + "' já declarada.");
        }

        Object valor = visit(ctx.operacao());
        if (!(valor instanceof Boolean)) { // Type checking
            throw new RuntimeException("Erro Semântico: A variável '" + nomeVar + "' deve receber verdadeiro ou falso.");
        }

        tabelaSimbolos.put(nomeVar, valor);
        return null;
    }

    @Override
    public Object visitCmdAtribuicao(UcsalParser.CmdAtribuicaoContext ctx) {
        String nomeVar = ctx.ID().getText();
        // Não deixa usar uma variavel que não existe
        if (!tabelaSimbolos.containsKey(nomeVar)) {
            throw new RuntimeException("Erro: Variável '" + nomeVar + "' não declarada.");
        }

        Object valorAtual = tabelaSimbolos.get(nomeVar);
        Object novoValor = visit(ctx.operacao());
        // Não deixa o usuário trocar o tipo da variavel no meio do código
        if (valorAtual.getClass() != novoValor.getClass()) {
            throw new RuntimeException("Erro de Tipo: Incompatibilidade na atribuição de '" + nomeVar + "'.");
        }

        tabelaSimbolos.put(nomeVar, novoValor);
        return null;
    }

    @Override
    public Object visitOpVariavel(UcsalParser.OpVariavelContext ctx) {
        String nomeVar = ctx.ID().getText();
        if (!tabelaSimbolos.containsKey(nomeVar)) {
            throw new RuntimeException("Erro: Variável '" + nomeVar + "' não declarada.");
        }
        return tabelaSimbolos.get(nomeVar);
    }

    @Override
    public Object visitOpNumero(UcsalParser.OpNumeroContext ctx) {
        return Integer.parseInt(ctx.NUMERO().getText());
    }

    @Override
    public Object visitOpVerdadeiro(UcsalParser.OpVerdadeiroContext ctx) {
        return true;
    }

    @Override
    public Object visitOpFalso(UcsalParser.OpFalsoContext ctx) {
        return false;
    }

    @Override
    public Object visitOpSomaSub(UcsalParser.OpSomaSubContext ctx) {
        // Resolve os dois lados garantindo que são números
        Integer esq = (Integer) visit(ctx.operacao(0));
        Integer dir = (Integer) visit(ctx.operacao(1));

        if (ctx.SOMA() != null) return esq + dir;
        return esq - dir;
    }

    @Override
    public Object visitOpMultDiv(UcsalParser.OpMultDivContext ctx) {
        Integer esq = (Integer) visit(ctx.operacao(0));
        Integer dir = (Integer) visit(ctx.operacao(1));

        if (ctx.MULT() != null) return esq * dir;
        return esq / dir;
    }

    @Override
    public Object visitOpRelacional(UcsalParser.OpRelacionalContext ctx) {
        Integer esq = (Integer) visit(ctx.operacao(0));
        Integer dir = (Integer) visit(ctx.operacao(1));

        if (ctx.MAIOR() != null) return esq > dir;
        if (ctx.MENOR() != null) return esq < dir;
        if (ctx.IGUAL() != null) return esq.equals(dir);
        if (ctx.DIF() != null) return !esq.equals(dir);

        return false;
    }

    @Override
    public Object visitBloco(UcsalParser.BlocoContext ctx) {
        // Executa todos os comandos que estiverem dentro das chaves { }
        for (UcsalParser.ComandoContext cmd : ctx.comando()) {
            visit(cmd);
        }
        return null;
    }

    @Override
    public Object visitCmdCaso(UcsalParser.CmdCasoContext ctx) {
        // Resolve a conta ou variável dentro dos parênteses
        Object condicao = visit(ctx.operacao());

        if (!(condicao instanceof Boolean)) {
            throw new RuntimeException("Erro Semântico: A condição do 'caso' precisa ser um booleano.");
        }

        boolean ehVerdadeiro = (Boolean) condicao;

        if (ehVerdadeiro) {
            visit(ctx.bloco(0)); // if
        } else if (ctx.bloco().size() > 1) {
            visit(ctx.bloco(1)); // else
        }

        return null;
    }

    @Override
    public Object visitCmdEnquanto(UcsalParser.CmdEnquantoContext ctx) {
        // Resolve a condição pela primeira vez
        Object condicao = visit(ctx.operacao());

        // Type Checking
        if (!(condicao instanceof Boolean)) {
            throw new RuntimeException("Erro Semântico: A condição do 'enquanto' precisa ser um booleano.");
        }

        // Laço
        while ((Boolean) condicao) {
            visit(ctx.bloco()); // Executa tudo que está dentro das chaves { }
            // Ve se a conta dentro dos parenteses deve continuar ou parar
            condicao = visit(ctx.operacao());
        }

        return null;
    }

    @Override
    public Object visitOpLer(UcsalParser.OpLerContext ctx) {
        // Imprime um > no console
        System.out.print("> ");
        // Lê a linha inteira digitada
        String entrada = teclado.nextLine();
        // Tenta converter para inteiro
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro: O comando ler() só suporta números inteiros.");
        }
    }
}