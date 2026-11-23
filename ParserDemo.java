public class ParserDemo {

	public static void main(String[] args) {
		String filePath = "prog1.jay";
		if(args != null && args.length > 0 && args[0] != null){
			filePath = args[0];
		}
		TokenStream tStream = new TokenStream(filePath);
		ConcreteSyntax cSyntax = new ConcreteSyntax(tStream);
		Program p = cSyntax.program();
		System.out.println(p.display());

	}

}