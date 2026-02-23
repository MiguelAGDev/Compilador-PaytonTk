/*:-----------------------------------------------------------------------------
 *:                        INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                      INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                          LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                      
 *:                
 *:          Clase con la funcionalidad del Analizador Sintactico
 * *:                            
 *: Archivo        : SintacticoSemantico.java
 *: Autor          : Fernando Gil  ( Estructura general de la clase  )
 *:                  Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha          : 03/SEP/2014
 *: Compilador     : Java JDK 7
 *: Descripción    : Esta clase implementa un parser descendente del tipo 
 *:                  Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                  No-Terminal de la gramatica mas el metodo emparejar ().
 *:                  El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * * INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        PROGRAMA();
    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  * * * * PEGAR AQUI EL CODIGO DE LOS PROCEDURES  * * * *
    //--------------------------------------------------------------------------

        
    //Luis Gael Fernández Dávalos
    public void PROGRAMA() {
        // PRIMEROS de INSTRUCCION: def, int, float, string, id, if, while, print
        if (preAnalisis.equals("def") || preAnalisis.equals("int") || preAnalisis.equals("float") || 
            preAnalisis.equals("string") || preAnalisis.equals("id") || preAnalisis.equals("if") || 
            preAnalisis.equals("while") || preAnalisis.equals("print")) {
            
            // PROGRAMA -> INSTRUCCION PROGRAMA
            INSTRUCCION();
            PROGRAMA();
        } else {
            // ϵ (Empty) - Fin del programa
            return; 
        }
    }

     //Luis Gael Fernández Dávalos
    public void INSTRUCCION() {
        if (preAnalisis.equals("def")) {
            // INSTRUCCION -> FUNCION
            FUNCION();
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string") || 
                   preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while") || 
                   preAnalisis.equals("print")) {
            // INSTRUCCION -> PROPOSICION
            PROPOSICION();
        } else {
            error("[INSTRUCCION] ERROR: Se esperaba función o proposición");
        }
    }

     //Luis Gael Fernández Dávalos
  public void FUNCION() {
        if (preAnalisis.equals("def")) {
            // FUNCION -> def id ( ARGUMENTOS ) : TIPO_RETORNO PROPOSICIONES_OPTATIVAS return RESULTADO ::
            emparejar("def");
            emparejar("id");
            emparejar("(");
            ARGUMENTOS();
            emparejar(")");
            emparejar(":");
            TIPO_RETORNO();
            PROPOSICIONES_OPTATIVAS();
            emparejar("return");
            RESULTADO();
            
            //  :: como dos tokens separados
            emparejar(":"); 
            emparejar(":"); 
        } else {
            error("[FUNCION] ERROR: Se esperaba 'def'");
        }
    }

  
   //Luis Gael Fernández Dávalos
    public void ARGUMENTOS() {
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            // ARGUMENTOS -> TIPO_DATO id ARGUMENTOS’
            TIPO_DATO();
            emparejar("id");
            ARGUMENTOSp();
        } else {
            // ϵ (Empty) - Puede no haber argumentos
            return; 
        }
    }

     //Luis Gael Fernández Dávalos
    public void ARGUMENTOSp() {
        if (preAnalisis.equals(",")) {
            // ARGUMENTOS’ -> , TIPO_DATO id ARGUMENTOS’
            emparejar(",");
            TIPO_DATO();
            emparejar("id");
            ARGUMENTOSp();
        } else {
            // ϵ (Empty)
            return; 
        }
    }
    
    //Ximena Arciniega Ochoa

    public void DECLARACION_VARS() {
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            // DECLARACION_VARS -> TIPO_DATO id DECLARACION_VARS’
            TIPO_DATO();
            emparejar("id");
            DECLARACION_VARSp();
        } else {
            error("[DECLARACION_VARS] ERROR: Se esperaba tipo de dato");
        }
    }

     //Ximena Arciniega Ochoa
    public void DECLARACION_VARSp() {
        if (preAnalisis.equals(",")) {
            // DECLARACION_VARS’ -> , id DECLARACION_VARS’
            emparejar(",");
            emparejar("id");
            DECLARACION_VARSp();
        } else {
            // ϵ (Empty)
            return; 
        }
    }

    

     //Ximena Arciniega Ochoa

    public void TIPO_DATO() {
        if (preAnalisis.equals("int")) {
            emparejar("int");
        } else if (preAnalisis.equals("float")) {
            emparejar("float");
        } else if (preAnalisis.equals("string")) {
            emparejar("string");
        } else {
            error("[TIPO_DATO] ERROR: Se esperaba int, float o string");
        }
    }

     //Ximena Arciniega Ochoa
    public void TIPO_RETORNO() {
        if (preAnalisis.equals("void")) {
            emparejar("void");
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            TIPO_DATO();
        } else {
            error("[TIPO_RETORNO] ERROR: Se esperaba void o tipo de dato");
        }
    }

     //Ximena Arciniega Ochoa
    public void RESULTADO() {
        // PRIMEROS de EXPRESION: id, num, num.num, (, literal
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || 
            preAnalisis.equals("(") || preAnalisis.equals("literal")) {
            EXPRESION();
        } else if (preAnalisis.equals("void")) {
            emparejar("void");
        } else {
            error("[RESULTADO] ERROR: Se esperaba expresion o void");
        }
    }

    //Marco Fernando Silva Rodriguez
    public void PROPOSICIONES_OPTATIVAS() {
        // PRIMEROS de PROPOSICION
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string") || 
            preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while") || 
            preAnalisis.equals("print")) {
            
            // PROPOSICIONES_OPTATIVAS -> PROPOSICION PROPOSICIONES_OPTATIVAS
            PROPOSICION();
            PROPOSICIONES_OPTATIVAS();
        } else {
            // ϵ (Empty)
            return; 
        }
    }

    //Marco Fernando Silva Rodriguez
   public void PROPOSICION() {
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            DECLARACION_VARS();
        } else if (preAnalisis.equals("id")) {
            emparejar("id");
            PROPOSICIONp();
        } else if (preAnalisis.equals("if")) {
            // PROPOSICION -> if CONDICION : PROPOSICIONES_OPTATIVAS else : PROPOSICIONES_OPTATIVAS ::
            emparejar("if");
            CONDICION();
            emparejar(":");
            PROPOSICIONES_OPTATIVAS();
            emparejar("else");
            emparejar(":");
            PROPOSICIONES_OPTATIVAS();
            
            
            emparejar(":"); 
            emparejar(":"); 
        } else if (preAnalisis.equals("while")) {
            // PROPOSICION -> while CONDICION : PROPOSICIONES_OPTATIVAS ::
            emparejar("while");
            CONDICION();
            emparejar(":");
            PROPOSICIONES_OPTATIVAS();
            
            
            emparejar(":"); 
            emparejar(":"); 
        } else if (preAnalisis.equals("print")) {
            emparejar("print");
            emparejar("(");
            EXPRESION();
            emparejar(")");
        } else {
            error("[PROPOSICION] ERROR: Proposición no válida");
        }
    }
   //Marco Fernando Silva Rodriguez

    public void PROPOSICIONp() {
        if (preAnalisis.equals("opasig")) {
            // PROPOSICION’ -> opasig EXPRESION
            emparejar("opasig");
            EXPRESION();
        } else if (preAnalisis.equals("(")) {
            // PROPOSICION’ -> ( LISTA_EXPRESIONES )
            emparejar("(");
            LISTA_EXPRESIONES();
            emparejar(")");
        } else {
            error("[PROPOSICIONp] ERROR: Se esperaba opasig o '('");
        }
    }

   //Marco Fernando Silva Rodriguez
    public void LISTA_EXPRESIONES() {
        // PRIMEROS de EXPRESION
        if (preAnalisis.equals("literal") || preAnalisis.equals("id") || preAnalisis.equals("num") || 
            preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            // LISTA_EXPRESIONES -> EXPRESION LISTA_EXPRESIONES’
            EXPRESION();
            LISTA_EXPRESIONESp();
        } else {
            // ϵ (Empty)
            return; 
        }
    }

    //Marco Fernando Silva Rodriguez
    public void LISTA_EXPRESIONESp() {
        if (preAnalisis.equals(",")) {
            // LISTA_EXPRESIONES’ -> , EXPRESION LISTA_EXPRESIONES’
            emparejar(",");
            EXPRESION();
            LISTA_EXPRESIONESp();
        } else {
            // ϵ (Empty)
            return; 
        }
    }

    //Marco Fernando Silva Rodriguez
    public void CONDICION() {
        // CONDICION -> EXPRESION oprel EXPRESION
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || 
            preAnalisis.equals("(") || preAnalisis.equals("literal")) {
            
            EXPRESION();
            emparejar("oprel");
            EXPRESION();
        } else {
            error("[CONDICION] ERROR: Se esperaba una expresión");
        }
    }

    //MIGUEL ANGEL AVILA GARCIA
    public void EXPRESION() {
        if (preAnalisis.equals("literal")) {
            // EXPRESION -> literal
            emparejar("literal");
        } else if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            // EXPRESION -> TERMINO EXPRESION’
            TERMINO();
            EXPRESIONp();
        } else {
            error("[EXPRESION] ERROR: Se esperaba término o literal");
        }
    }
        //MIGUEL ANGEL AVILA GARCIA


    public void EXPRESIONp() {
        if (preAnalisis.equals("opsuma")) {
            // EXPRESION’ -> opsuma TERMINO EXPRESION'
            emparejar("opsuma");
            TERMINO();
            EXPRESIONp();
        } else {
            // ϵ (Empty) - Termina la expresión (no es error)
            return; 
        }
    }

        //MIGUEL ANGEL AVILA GARCIA

    public void TERMINO() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            // TERMINO -> FACTOR TERMINO’
            FACTOR();
            TERMINOp();
        } else {
            error("[TERMINO] ERROR");
        }
    }

        //MIGUEL ANGEL AVILA GARCIA

    public void TERMINOp() {
        if (preAnalisis.equals("opmult")) {
            // TERMINO’ -> opmult FACTOR TERMINO’
            emparejar("opmult");
            FACTOR();
            TERMINOp();
        } else {
            // ϵ (Empty) 
            return; 
        }
    }

        //MIGUEL ANGEL AVILA GARCIA

    public void FACTOR() {
        if (preAnalisis.equals("id")) {
            // FACTOR -> id FACTOR’
            emparejar("id");
            FACTORp();
        } else if (preAnalisis.equals("num")) {
            // FACTOR -> num
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            // FACTOR -> num.num
            emparejar("num.num");
        } else if (preAnalisis.equals("(")) {
            // FACTOR -> ( EXPRESION )
            emparejar("(");
            EXPRESION();
            emparejar(")");
        } else {
            error("[FACTOR] ERROR");
        }
    }

        //MIGUEL ANGEL AVILA GARCIA

    public void FACTORp() {
        if (preAnalisis.equals("(")) {
            // FACTOR’ -> ( LISTA_EXPRESIONES )
            emparejar("(");
            LISTA_EXPRESIONES();
            emparejar(")");
        } else {
            // ϵ (Empty)
            return; 
        }
    }
    
    
    
}
//------------------------------------------------------------------------------
//::