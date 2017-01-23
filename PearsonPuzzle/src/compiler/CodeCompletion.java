package compiler;

public class CodeCompletion {
	
	public static String completeCode(String code, String onlineImports, String methodsImport){
		code=onlineImports+code;
		return code;
	}

}
