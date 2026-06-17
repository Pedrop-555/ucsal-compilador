import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) {
    String codigoTexto = """ 
            inteiro contador = 0;
            enquanto (contador < 3) {
                caso (contador == 1) {
                    imprimir(999);}
                casonao { //Ignora comentarios
                    imprimir(contador);}
                contador = contador + 1;}"""; // Codigo de teste

        // Lexer e Parser
        UcsalLexer lexer = new UcsalLexer(CharStreams.fromString(codigoTexto));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        UcsalParser parser = new UcsalParser(tokens);
        UcsalParser.ProgramaContext arvore = parser.programa();

        // Análise Semântica
        AnalisadorVisitor checador = new AnalisadorVisitor();
        checador.visit(arvore); // Se tiver erro, o programa trava aqui e nem compila

        // Geração do Bytecode
        GeradorCodigoVisitor gerador = new GeradorCodigoVisitor();
        gerador.visit(arvore);

        System.out.println("--- Executando na Máquina Virtual UCSAL ---");

        // Codigo final na VM
        MaquinaVirtual vm = new MaquinaVirtual(gerador.linhasDeCodigo);
        vm.executar();
    }
}