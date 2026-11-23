public class ParserDemo {

	public static void main(String[] args) {
		String filePath = "prog1.jay";
		if(args.length > 0){
			filePath = args[0];
		}
		TokenStream tStream = new TokenStream(filePath);
		ConcreteSyntax cSyntax = new ConcreteSyntax(tStream);
		Program p = cSyntax.program();
		System.out.println(p.display());

	}

}