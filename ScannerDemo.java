
/**
 * @author scharffc
 * 
 */
public class ScannerDemo {
	private static String file1 = "prog1.jay";
	private static int counter = 1;

	public static void main(String args[]) {
		if(args.length > 0){
			file1 = args[0];
		}

		TokenStream ts = new TokenStream(file1);
		System.out.println(file1);
		while (!ts.isEndofFile()) {
			// TO BE COMPLETED
			Token tk = ts.nextToken();
			if (tk == null || tk.getValue() == null || tk.getValue().equals("")){
				continue;
			}
			System.out.println("Token " + counter++ + " - Type: " + tk.getType() + " - Value: " + tk.getValue());
		}
	}
}
