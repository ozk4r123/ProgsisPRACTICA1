import java.util.*;
import java.io.*;

public class Progsis
{
	Scanner Leer=new Scanner(System.in);
	String ruta=null, texto=null, archivo=null,Archivo_Instrucciones=null,Archivo_Error=null;
	boolean fin;
	int linea=0;
	Vector<Tabop> tabop_vector=new Vector<Tabop>();

	public Progsis(String r)
	{
		ruta=r;
	}

	public Progsis() {}

	public void validarRuta()
	{
		File fichero = new File(ruta);
        if (fichero.exists())
        {
        	if(fichero.getName().endsWith(".asm") || fichero.getName().endsWith(".ASM"))
        	{
        		leerTabop();
        	    crearArchivos();
        	    leerArchivos();
        	}
            else
            {
        		System.out.println("El archivo no tiene la extensi�n .ASM");
        	}
        }
        else
        {
        	System.out.println("La ruta no existe");
        }
     }
	
	public void crearArchivos()
	{
		File fichero = new File(ruta);
		Archivo_Instrucciones=fichero.getName().substring(0,fichero.getName().indexOf('.'))+".INST";
		Archivo_Error=fichero.getName().substring(0,fichero.getName().indexOf('.'))+".ERR";
		File inst = new File(Archivo_Instrucciones);
		File err = new File(Archivo_Error);
		if(inst.exists())
		{
			inst.delete();
		}
		if(err.exists())
		{
			err.delete();
		}
		try
		{
			RandomAccessFile archinst=new RandomAccessFile(Archivo_Instrucciones,"rw");
			archinst.seek(0);
			archinst.writeBytes("LINEA      ETQ      CODOP      OPER      MODOS");
			archinst.writeBytes("\r\n----------------------------------------------------\r\n");
			archinst.close();
		}
		catch(IOException ioe)
		{
			System.out.println("Error al crear archivo de instrucciones");
		}
	    try
	    {
			RandomAccessFile archierr=new RandomAccessFile(Archivo_Error,"rw");
			archierr.seek(0);
			archierr.writeBytes("                   ERRORES                     ");
			archierr.writeBytes("\r\n----------------------------------------------------\r\n");
			archierr.close();
		}
	    catch(IOException ioe)
	    {
	    	System.out.println("Error al crear el archivo de errores");
	    }
	}

	public void leerArchivos()
	{
		try
		{
			RandomAccessFile archivo_asm=new RandomAccessFile(new File(ruta),"r");
			archivo_asm.seek(3);
			while(archivo_asm.getFilePointer()!=archivo_asm.length() && !fin)
			{
				texto=archivo_asm.readLine();
				linea++;
				System.out.println("Revisando linea no: "+linea);
				Maquina_estados();
			}
			if(!fin)
			{
				escribirError("\nNo hay Fin",Archivo_Error);
			}
			archivo_asm.close();
	    }
        catch(IOException e)
        {
        	System.out.println("Error al leer el archivo");
        }
	}

    public void Maquina_estados()
    {
    	Linea lin= new Linea(linea,Archivo_Error);
    	String etiq=null,codop=null,oper=null,modos=null;
        int contador=0, estado=0, tama�o=texto.length();
        char[] cad = texto.toCharArray();
        boolean eti=false, cod=false,cod_END=false, op=false;

         while(estado != 9)
         {
        	switch(estado)
        	{
	    		case 0:
	    			System.out.println("Entra a caso 0");
	    			if(contador==tama�o)
	    			{
	    				estado=9;
	    			}
	    			else if(cad[contador]==';')
	    			{
	    				estado=1;
	    				contador++;
				    }
					else if(cad[contador]==' ' || cad[contador]=='\t')
					{
						estado=3;
						etiq="NULL";
						eti=true;
						contador++;
					}
					else
					{
						etiq=cad[contador]+"";
						contador++;
						estado=2;
					}
			     break;
    		     case 1:
    		    	 System.out.println("Entra a caso 1");
    		    	 if(tama�o==contador)
    		     	 {
    		    		 estado=9;
    		     	 }
    		     	 else
    		     	 {
    		     		 contador++;
    		     		 estado=1;
    		     	 }
     	         break;
     	         case 2:
     	        	System.out.println("Entra a caso 2");
     	         	if(contador==tama�o)
     	         	{
     	         		escribirError(linea +"\tNo hay Codigo de operacion \r\n",Archivo_Error);
     	         		estado=9;
     	         	}
     	         	else if(cad[contador]==';')
     	         	{
 	         			escribirError(linea+"\tNo hay codigo de operacion\r\n",Archivo_Error);
 	         			estado=1;
 	         			contador++;
     	         	}
     	         	else if(cad[contador]==' ' || cad[contador]=='\t')
     	         	{
     	         		eti=lin.validarEtiqueta(etiq);
     	         		contador++;
     	         		estado=4;
     	         	}
     	         	else
     	         	{
     	         		etiq+=cad[contador];
     	         		contador++;
         		        estado=2;
     	         	}
     	         break;
     	         case 3:
     	        	System.out.println("Entra a caso 3");
     	        	 if(tama�o==contador)
     	        	 {
     	        		 estado=9;
	 	         	 }
     	         	 else if(cad[contador]==';')
     	         	 {
	     	         	 estado=1;
	     	         	 contador++;
     	         	 }
     	         	 else if(cad[contador]==' ' || cad[contador]=='\t')
     	         	 {
	         			 contador++;
	         			 estado=3;
     	         	 }
     	         	 else
     	         	 {
     	         		 if((cad[contador]=='E'||cad[contador]=='e')&& (contador+3)<=tama�o)
	         			 {
     	         			 String subStr=texto.substring(contador, contador+3);
     	         			 if(subStr.toUpperCase().equals("END"))
     	         			 {
     	         				fin=true;
     	         				 estado=9;
     	         			 }
	         			 }
     	         		 if(!fin)
     	         		 {
     	         			 codop=cad[contador]+"";
     	         			 contador++;
     	         			 estado=5;
     	         		 }
     	         	 }
     	         break;
     	         case 4:
     	        	System.out.println("Entra a caso 4");
     	        	 if(tama�o==contador)
     	        	 {
	 	         		 escribirError(linea+"\tNo hay codigo de operacion \r\n",Archivo_Error);
	 	                 estado=9;
     	         	 }
     	         	 else if(cad[contador]==';')
     	         	 {
     	         		 escribirError(linea+"\tNo hay codigo de operacion \r\n",Archivo_Error);
     	                 estado=1;
     	                 contador++;
     	         	 }
     	         	 else if(cad[contador]==' '|| cad[contador]=='\t')
     	         	 {
     	         		 contador++;
     	         		 estado=4;
     	         	 }
     	         	else
     	         	{
	    	         	codop=cad[contador]+"";
	    	         	contador++;
	    	         	estado=5;
     	         	}
     	         break;
     	         case 5:
     	        	System.out.println("Entra a caso 5");
     	        	 if(tama�o==contador)
     	        	 {
     	        		 cod=lin.validarCodigo(codop);
     	        		 cod_END=lin.validarCodigo_END(codop);
     	        		 estado=9;
     	        		 op=true;
     	        		 oper="NULL";
     	        	 }
     	        	 else if(cad[contador]==';')
     	        	 {
     	        		 estado=1;
	     	         	 cod=lin.validarCodigo(codop);
	     	         	 cod_END=lin.validarCodigo_END(codop);
	     	         	 op=true;
	     	         	 oper="NULL";
     	         	 }
     	         	 else if(cad[contador]==' '|| cad[contador]=='\t')
     	         	 {
     	         		 cod=lin.validarCodigo(codop);
     	         		cod_END=lin.validarCodigo_END(codop);
     	         		 contador++;
     	         		 estado=6;
     	         	 }
     	         	 else
     	         	 {
     	         		 codop=codop+cad[contador];
     	         		cod_END=lin.validarCodigo_END(codop);
     	         		 estado=5;
     	         		 contador++;
     	         	 }
     	         break;
     	         case 6:
     	        	System.out.println("Entra a caso 6");
     	        	 if(tama�o==contador)
     	         	 {
     	        		 estado=9;
     	        		 oper="NULL";
     	         		 op=true;
     	         	 }
     	         	 else if(cad[contador]==' '|| cad[contador]=='\t')
     	         	 {
     	         		 contador++;
     	         		 estado=6;
     	         	 }
     	         	 else if(cad[contador]==';')
     	         	 {
	         			 contador++;
	         		 	 estado=1;
	         		 	 op=true;
	         			 oper="NULL";
     	         	 }
     	         	 else
     	         	 {
     	         		 oper=cad[contador]+"";
     	         		 contador++;
     	         		 estado=7;
     	         	 }
     	         break;
     	         case 7:
     	        	System.out.println("Entra a caso 7");
     	        	 if(tama�o==contador)
     	        	 {
     	        		 op=lin.validarOperando(oper);
     	         		 estado=9;
     	         	 }
     	         	 else if(cad[contador]==';')
     	         	 {
     	         		 op=lin.validarOperando(oper);
     	         		 estado=1;
     	         		 contador++;
     	         	 }
     	         	 else if(cad[contador]==' ' || cad[contador]=='\t')
     	         	 {
     	         		op=lin.validarOperando(oper);
     	         		contador++;
     	         		estado=8;
     	         	 }
     	         	 else if(cad[contador]==';')
     	         	 {
     	         		 op=lin.validarOperando(oper);
     	         		 contador++;
     	         		 estado=1;
     	         	 }
     	         	 else
     	         	 {
     	         		 oper+=cad[contador];
     	         		 contador++;
     	         	 }
     	         break;
     	         case 8:
     	        	System.out.println("Entra a caso 8");
     	        	 if(tama�o==contador)
     	        	 {
     	         		 estado=9;
     	         	 }
     	         	 else if(cad[contador]==' ' || cad[contador]=='\t')
     	         	 {
     	         		 contador++;
     	         	 }
     	         	 else if(cad[contador]==';')
     	         	 {
     	         		 contador++;
     	         		 estado=1;
     	         	 }
     	         	 else
     	         	 {
 	     				escribirError(linea+"\tExceso    de    operandos\r\n",Archivo_Error);
 	     				estado=9;
 	     			 }
     	         break;
        	}
         }
	     if((eti && cod) && op)
	     {
	    	 modos=buscarCodop(codop,oper);
	    	 if(modos != null)
 			 {
 				if(etiq==null)
 					etiq="NULL";
 				if(oper==null)
 					oper="NULL";
 				escribirInstruccion(linea+  "	"+ etiq+"	"+codop+"	"+oper+"	"+modos+"\r\n");
 			 }
	     }
	     if(cod_END)
	     {
	    	 fin=true;
	     }
    }

    public void escribirError(String error, String archi)
    {
    	Archivo_Error=archi;
    	try
    	{
			RandomAccessFile archierr=new RandomAccessFile(Archivo_Error,"rw");
			archierr.seek(archierr.length());
			archierr.writeBytes(error);
			archierr.close();
		}
	    catch(IOException e)
	    {
		    System.out.println("Error al escribir el error");
	    }
    }

    public void escribirInstruccion(String instruccion)
    {
    	try
    	{
    		RandomAccessFile archinst=new RandomAccessFile(Archivo_Instrucciones,"rw");
    		archinst.seek(archinst.length());
			archinst.writeBytes(instruccion);
			archinst.close();
		}
	    catch(IOException e)
	    {
		    System.out.println("Error al escribir la instrucci�n");
	    }
    }
    public void leerTabop()
    {
    	String linea_tabop,ruta_tabop;
    	Scanner Leer=new Scanner(System.in);
    	System.out.println("Escribe la ruta del archivo tabop");
    	ruta_tabop=Leer.nextLine();

    	try
    	{
    		RandomAccessFile archivo_tabop=new RandomAccessFile(new File(ruta_tabop),"r");
			while(archivo_tabop.getFilePointer()!=archivo_tabop.length())
			{
				Tabop tb=new Tabop();
				linea_tabop=archivo_tabop.readLine();
				StringTokenizer st = new StringTokenizer(linea_tabop,"|");
				tb.agregar(st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken());
				tabop_vector.addElement(tb);
		   	}
			archivo_tabop.close();
    	}
	    catch(IOException e)
        {
	      	System.out.println("Error, no se encontro el archivo tabop");
        }
    }
    public String buscarCodop(String c,String op)
    {
    	boolean bcod=false,band=true;
    	int cont=0;
    	String modos=null;
    	if(c.toUpperCase().equals("ORG") || c.toUpperCase().equals("END"))
    	{
    		return " ";
    	}
    	else
    	{
	    	for(int i=0; i<tabop_vector.size() && band; i++)
	    	{
				Tabop t=new Tabop();
				t=tabop_vector.elementAt(i);
				if(c.toUpperCase().equals(t.codigo_oper))
				{
					System.out.println("Se encontro un codigo de operacion");
					bcod=true;
					cont++;
					if(cont==1)
					{
						modos=t.cod2;
						if(t.cod2.equals("1")&& op==null)
						{
							escribirError(linea+"\tEl codigo de operacion "+c+" requiere operando\r\n",Archivo_Error);
							return null;
						}
						else if(t.cod2.equals("0")&& op!=null)
						{
							escribirError(linea+"\tEl codigo de operacion "+c+" no requiere operando\r\n",Archivo_Error);
							return null;
						}					
					}
					else
					{
						modos+=","+t.cod2;
					}
				}
				else
				{
					if(cont>1)
					{
						band=false;
					}
				}
    		}
		} 
	    if(!bcod)
	    {
	    	escribirError(linea+ "\tEl codigo de operacion "+c+" no se encuentra en el tabop\r\n",Archivo_Error);
	    }
		return modos;
    }

    public static void main(String[] args)
    {
    	Scanner Leer=new Scanner(System.in);
    	String ruta;
    	System.out.println("Ingresa la ruta del archivo(extensi�n .ASM)");
    	ruta=Leer.nextLine();
    	Progsis programa= new Progsis(ruta);
    	programa.validarRuta();
    	System.out.println("Operacion completada");
    }
}