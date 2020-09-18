package javax.servlet;

import java.io.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

public interface ServletInputStream {
	public int readLine(byte b[],int i,int t) throws java.io.IOException; 
	public int read(byte[] buf) throws IOException;
	public int read(byte[] buf,int i,int t) throws IOException;
	public int leB(byte b[]) throws java.io.IOException;
}
