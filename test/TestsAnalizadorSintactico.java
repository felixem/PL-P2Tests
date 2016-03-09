import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

@RunWith(Parameterized.class)
public class TestsAnalizadorSintactico extends TestCase
{
	//Excepción de salida
    protected static class ExitException extends SecurityException 
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final int status;
        public ExitException(int status) 
        {
            super("There is no escape!");
            this.status = status;
        }
    }

    //Security Manager preparado para evitar la salida
    private static class NoExitSecurityManager extends SecurityManager 
    {
        @Override
        public void checkPermission(Permission perm) 
        {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context) 
        {
            // allow anything.
        }
        @Override
        public void checkExit(int status) 
        {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }
    
    
    //Buffers para usar en entrada-salida
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    
    @Override
    @Before
    public void setUp() throws Exception 
    {
        super.setUp();
      //Redirigir buffers de entrada-salida
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        //Cambiar gestor de seguridad
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @Override
    @After
    public void tearDown() throws Exception 
    {
    	//Cambiar sistema de seguridad
        System.setSecurityManager(null); // or save and restore original
        //Limpiar buffers
        System.setOut(null);
        System.setErr(null);
        super.tearDown();
    }

    @Parameters(name = "{index}: fuente:{0}")
    //Parámetros de los distintos tests
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {  
        	//Pruebas del profesor
                 {"resources/fuentes/p01.txt","","Error lexico (1,5): caracter '.' incorrecto", -1 },
                 {"resources/fuentes/p02.txt","", "Error sintactico (6,3): encontrado 'b', esperaba * / + - ; ", -1 },
                 {"resources/fuentes/p03.txt","1 7 8 9 10 17 7 9 8 9 10 17 7 8 9 10 16 18 21 22 19 21 24 11 12 15 6 11 12 15 5 16 19 21 22 11 12 15 6 2 3 4 7 8 8 9 9 9 9 10 17 7 8 9 9 10 16 19 21 24 16 19 21 24 11 12 15 6 16 19 20 24 21 22 16 18 20 23 21 23 19 21 23 16 18 21 23 19 21 23 11 12 15 6 11 13 14 15 12 15 5 6 ", 
                	"", 0 },
                 {"resources/fuentes/p04.txt","1 7 8 9 9 10 16 19 21 24 17 7 8 9 9 10 17 7 8 9 10 16 19 21 23 11 12 15 5 16 19 21 23 11 12 15 6 11 12 15 5 3 ",
                	"", 0 },
                 {"resources/fuentes/p05.txt","1 7 8 8 8 8 8 8 8 8 9 9 9 9 9 8 9 8 9 8 9 9 10 16 19 20 22 21 24 16 19 21 24 11 12 15 6 16 19 21 23 11 12 15 5 16 19 21 22 11 12 15 6 16 18 21 23 19 21 23 16 18 21 22 19 21 22 16 18 21 22 19 21 23 16 18 21 23 19 21 22 16 18 21 23 19 21 23 11 12 15 5 11 12 15 5 11 12 15 5 11 12 15 5 11 12 15 6 11 12 15 6 11 12 15 6 11 12 15 6 3 ",
                	"", 0 },
                 {"resources/fuentes/p06.txt","1 7 8 8 9 9 9 9 9 9 10 16 18 20 24 20 22 21 22 19 21 24 16 18 21 24 19 21 24 16 19 20 24 21 22 16 19 21 22 16 19 20 23 21 24 16 19 21 23 11 12 15 6 11 12 15 5 3 ",
                	"", 0 },
                 {"resources/fuentes/p07.txt","","Error lexico (5,6): caracter '.' incorrecto", -1 },
                 {"resources/fuentes/p08.txt","","Error lexico (5,11): caracter ':' incorrecto", -1 },
                 {"resources/fuentes/p09.txt","","Error lexico: fin de fichero inesperado", -1 },
                 {"resources/fuentes/p10.txt","","Error sintactico (1,6): encontrado 'main', esperaba ( ", -1 },
                 //Pruebas propias
                 {"resources/fuentes/felixem/comentarios01.txt","1 7 10 3 ","", 0 },
                 {"resources/fuentes/felixem/vacio.txt","","Error sintactico: encontrado fin de fichero, esperaba 'double' 'int' 'main' ", -1 },
                 {"resources/fuentes/felixem/main01.txt","","Error sintactico (1,1): encontrado 'long', esperaba 'double' 'int' 'main' ", -1 },
                 {"resources/fuentes/felixem/main02.txt","","Error sintactico (2,1): encontrado '{', esperaba ( ", -1 },
                 {"resources/fuentes/felixem/main03.txt","","Error sintactico (1,5): encontrado ')', esperaba ( ", -1 },
                 {"resources/fuentes/felixem/main04.txt","","Error sintactico: encontrado fin de fichero, esperaba { ", -1 },
                 {"resources/fuentes/felixem/main05.txt","","Error sintactico: encontrado fin de fichero, esperaba { } 'double' 'int' identificador ", -1 },
                 {"resources/fuentes/felixem/main06.txt","","Error sintactico (3,6): encontrado 'double', esperaba identificador ", -1 },
                 {"resources/fuentes/felixem/main07.txt","","Error sintactico (3,9): encontrado '(', esperaba ; , [ ", -1 },
                 {"resources/fuentes/felixem/main08.txt","","Error sintactico (3,10): encontrado ']', esperaba numero entero ", -1 },
                 {"resources/fuentes/felixem/main09.txt","","Error sintactico (3,10): encontrado '1.3', esperaba numero entero ", -1 },
                 {"resources/fuentes/felixem/main10.txt","","Error sintactico (3,12): encontrado ';', esperaba ] ", -1 },
                 {"resources/fuentes/felixem/main11.txt","","Error sintactico (3,13): encontrado 'a', esperaba numero entero ", -1 },
                 {"resources/fuentes/felixem/main12.txt","","Error sintactico (3,10): encontrado 'double', esperaba identificador ", -1 },
                 {"resources/fuentes/felixem/main13.txt","","Error sintactico (4,2): encontrado '1.3', esperaba { } 'double' 'int' identificador ", -1 },
                 {"resources/fuentes/felixem/main14.txt","","Error sintactico (4,2): encontrado '[', esperaba { } 'double' 'int' identificador ", -1 },
                 {"resources/fuentes/felixem/main15.txt","","Error sintactico (4,5): encontrado ';', esperaba = ", -1 },
                 {"resources/fuentes/felixem/main16.txt","","Error sintactico (4,8): encontrado ';', esperaba numero entero identificador numero real ", -1 },
                 {"resources/fuentes/felixem/main17.txt","","Error sintactico (4,9): encontrado '(', esperaba * / + - ; ", -1 },
                 {"resources/fuentes/felixem/main18.txt","","Error sintactico (4,12): encontrado ';', esperaba numero entero identificador numero real ", -1 },
                 {"resources/fuentes/felixem/main19.txt","","Error sintactico (4,12): encontrado ';', esperaba numero entero identificador numero real ", -1 },
                 {"resources/fuentes/felixem/main20.txt","","Error sintactico (4,13): encontrado '(', esperaba * / + - ; ", -1 },
                 {"resources/fuentes/felixem/main21.txt","","Error sintactico (4,11): encontrado '(', esperaba * / + - ; ", -1 },
                 {"resources/fuentes/felixem/main22.txt","","Error sintactico (4,9): encontrado '(', esperaba * / + - ; ", -1 },
                 {"resources/fuentes/felixem/main23.txt","","Error sintactico (6,6): encontrado ';', esperaba identificador ", -1 },
                 {"resources/fuentes/felixem/main24.txt","","Error sintactico (3,8): encontrado '=', esperaba ; , [ ", -1 },
                 {"resources/fuentes/felixem/main25.txt","","Error sintactico (3,14): encontrado 'a', esperaba numero entero ", -1 },
                 {"resources/fuentes/felixem/main26.txt","","Error sintactico: encontrado fin de fichero, esperaba { } 'double' 'int' identificador ", -1 },
                 {"resources/fuentes/felixem/main27.txt","","Error sintactico (4,5): encontrado '[', esperaba = ", -1 },
                 {"resources/fuentes/felixem/main28.txt","1 7 8 9 10 17 7 9 8 10 11 13 15 12 15 6 16 18 21 23 19 20 23 21 23 11 12 14 14 15 6 2 3 4 7 8 9 10 16 18 21 23 19 20 23 20 23 21 23 11 12 15 5 5 ","", 0 },
                 {"resources/fuentes/felixem/funcion01.txt","","Error sintactico (1,5): encontrado '33', esperaba identificador ", -1 },
                 {"resources/fuentes/felixem/funcion02.txt","","Error sintactico (1,12): encontrado ';', esperaba ( ", -1 },
                 {"resources/fuentes/felixem/funcion03.txt","","Error sintactico (1,13): encontrado ';', esperaba ) ", -1 },
                 {"resources/fuentes/felixem/funcion04.txt","","Error sintactico: encontrado fin de fichero, esperaba { } 'double' 'int' identificador ", -1 },
                 {"resources/fuentes/felixem/funcion05.txt","","Error sintactico (5,1): encontrado 'funcion', esperaba 'double' 'int' 'main' ", -1 },
                 {"resources/fuentes/felixem/funcion06.txt","","Error sintactico: encontrado fin de fichero, esperaba 'double' 'int' 'main' ", -1 },
                 {"resources/fuentes/felixem/funcion07.txt","","Error sintactico (1,5): encontrado 'main', esperaba identificador ", -1 },
                 {"resources/fuentes/felixem/funcion08.txt","","Error sintactico (1,13): encontrado 'int', esperaba ) ", -1 },
                 {"resources/fuentes/felixem/funcion09.txt","","Error sintactico (5,1): encontrado 'int', esperaba fin de fichero ", -1 },
                 {"resources/fuentes/pacocr/main01.txt","","Error sintactico (1,1): encontrado 'ma', esperaba 'double' 'int' 'main' ",-1},
           });
    }

    //Atributos del test
    private String fichero;
    private String salidaEsperada;
    private String errorEsperado;
    private int codigoErrorEsperado;
    
    //Constructor por parámetros
	public TestsAnalizadorSintactico(String fichero, String salidaEsperada, String errorEsperado, int codigoErrorEsperado) {
		super();
		this.fichero = fichero;
		this.salidaEsperada = salidaEsperada;
		this.errorEsperado = errorEsperado;
		this.codigoErrorEsperado = codigoErrorEsperado;
	}
	
	//Comparar dos listas de tokens
	public void compareTokensList(List<Token> expectedTokens, List<Token> resultTokens)
	{
		Assert.assertEquals("Cantidad de tokens", expectedTokens.size(), resultTokens.size());
		//Comparar token a token
		for(int i=0; i<expectedTokens.size(); ++i)
		{
			Token expected = expectedTokens.get(i);
			Token result = resultTokens.get(i);
			//Comparar atributos
			Assert.assertEquals("Token "+i+" Fila",expected.fila, result.fila);
			Assert.assertEquals("Token "+i+" Columna",expected.columna, result.columna);
			Assert.assertEquals("Token "+i+" Lexema",expected.lexema, result.lexema);
			Assert.assertEquals("Token "+i+" Tipo",expected.tipo, result.tipo);
		}
	}

    //Tests lexicos del profesor
	@Test
	public void test() throws Exception
	{
        try 
        {
        	plp2.main(new String[]{fichero});
        }
        catch(ExitException e)
        {
            //Comparar salida esperada y error esperado
        	Assert.assertEquals("Código de error esperado", codigoErrorEsperado, e.status);
            Assert.assertEquals("Salida esperada",salidaEsperada, outContent.toString());
            Assert.assertEquals("Error esperado",errorEsperado, errContent.toString());
            return;
        }

        //Comprobar si se esperaba error
        if(codigoErrorEsperado != 0)
        	Assert.fail("Se esperaba que el programa saliera con código de error "+codigoErrorEsperado);

        Assert.assertEquals("Salida esperada",salidaEsperada, outContent.toString());
        Assert.assertEquals("Error esperado",errorEsperado, errContent.toString());
	}

}

