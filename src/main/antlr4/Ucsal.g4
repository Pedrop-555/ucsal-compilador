grammar Ucsal;

// Regras sintáticas (Parser)
programa: comando+ EOF;

bloco: '{' comando* '}';

comando: 'imprimir' '(' operacao ')' ';'                  # CmdImprimir
       | 'inteiro' ID '=' operacao ';'                    # CmdDeclInteiro
       | 'booleano' ID '=' operacao ';'                   # CmdDeclBooleano
       | ID '=' operacao ';'                              # CmdAtribuicao
       | 'caso' '(' operacao ')' bloco ('casonao' bloco)? # CmdCaso
       | 'enquanto' '(' operacao ')' bloco                # CmdEnquanto
       ;

operacao: operacao (MULT | DIV) operacao              # OpMultDiv
        | operacao (SOMA | SUB) operacao              # OpSomaSub
        | operacao (MAIOR | MENOR | IGUAL | DIF) operacao # OpRelacional
        | NUMERO                                      # OpNumero
        | VERDADEIRO                                  # OpVerdadeiro
        | FALSO                                       # OpFalso
        | ID                                          # OpVariavel
        | 'ler' '(' ')'                               # OpLer
        ;

// Regras léxicas (Lexer)
VERDADEIRO: 'verdadeiro';
FALSO: 'falso';

MAIOR: '>';
MENOR: '<';
IGUAL: '==';
DIF: '!=';

MULT: '*';
DIV:  '/';
SOMA: '+';
SUB:  '-';

NUMERO: [0-9]+;
ID: [a-zA-Z_][a-zA-Z0-9_]*;

// Ignora espaços, tabs e quebras de linha
WS: [ \t\r\n]+ -> skip;

// Lê o // e descarta tudo até achar uma quebra de linha
COMENTARIO_LINHA: '//' ~[\r\n]* -> skip;