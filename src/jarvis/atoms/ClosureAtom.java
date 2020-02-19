package jarvis.atoms;

import jarvis.interpreter.JarvisEnvironment;
import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;
import java.util.Stack;


//SOUSLECAPOT
/*
 * Repr�sentation d'une fermeture.
 * Une fermeture est une suite de commandes qui est �valu�e
 * dans un environnement, avec des param�tres.
 * Elle repr�sente une �valuation diff�r�e.
 * En effet, l'interpr�teur ne fait pas les commandes
 * du corps de la fermeture pendant que vous les entrez.
 * Il les fera plus tard, lorsque la fermeture sera 
 * interpr�t�e avec des arguments associ�s � ses param�tres. 
 */

public class ClosureAtom extends AbstractAtom {

	private ArrayList<String> params;

	private Stack<String> body;

	private ClosureAtom() {
	}

	public ClosureAtom(ArrayList<String> p, Stack<String> b) {
		params = p;
		body = b;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractAtom interpretNoPut(JarvisInterpreter ji) {

		/*
		 * V�rification pour le nombre d'arguments.
		 * Une fermeture doit �tre �valu�e avec exactement le bon
		 * nombre d'arguments dans la file.
		 */

		if (params.size() != ji.getArgCount()) {
			throw new IllegalArgumentException("Closure "+ji.getEnvironment().reverseLookup(this)+": Bad number of arguments, expected " + params.size()
					+ " got " + (ji.getArgCount()));
			
		}

		/*
		 * Fabrique un nouvel environnement qui contiendra
		 * les arguments. Les symboles choisis pour les param�tres
		 * se trouvent li�s aux valeurs des arguments ci-apr�s.
		 */
		JarvisEnvironment closureEnvironment = new JarvisEnvironment(ji,
				ji.getEnvironment());

		for (String id : params) {
			AbstractAtom val = ji.getArg();	
			closureEnvironment.put(id, val);
		}

		

		/*
		 * Change l'environnement d'interpr�tation courant 
		 */
		ji.setEnvironment(closureEnvironment);

		/*
		 * Copie les commandes sur la pile de l'intepr�teur.
		 * En-dessous doit se trouver une commande sp�ciale
		 * qui ferme l'environnement dans lequel on vient d'entrer.
		 * 
		 */
		ji.pushEval(CommandAtom.CMD_ENDOFENVIRONMENT);
		Stack<String> temp = (Stack<String>) body.clone();
		while (!temp.isEmpty()) {
			ji.pushEval(temp.pop());
		}
		
		return this;
	}

		
	@Override
	public String makeKey() {

		String p = "(";
		for (String s : params) {
			p += (s + " ");
		}
		p = p.trim();

		
		p = p + "){" + body + "}";

		return p;
	}

	public static ClosureAtom read(JarvisInterpreter ji) {
		ClosureAtom closure = new ClosureAtom();
		closure.params = new ArrayList<String>();
		closure.body = new Stack<String>();

	
		String s = ji.nextInput();
		while (!(s.compareTo(".") == 0)) {

			closure.params.add(s);
			s = ji.nextInput();
		}

	
		s = ji.nextInput();
		int nestingCounter=1;
		while (nestingCounter>0) {

			if(s.compareTo(CommandAtom.CMD_CLOSURE)==0)
			{
				nestingCounter++;
			}
			else if(s.compareTo(CommandAtom.CMD_ENDOFCLOSURE)==0)
			{
				nestingCounter--;
				if(nestingCounter==0)
				{
					continue;
				}
			}
			
			closure.body.push(s);
			s = ji.nextInput();
		}
		

		return closure;
	}
	
	/*
	 * Cas sp�cial d'interpr�tation.
	 * Lorsqu'une closure est interpr�t�e, ne doit pas enfiler ce qu'elle
	 * retourne (elle-m�me) dans la file d'arguments.
	 * Les r�sultats de l'ex�cution de ses �nonc�s sont enfil�s par effet de bord
	 * des interpr�tations d�coulant de ces �nonc�s.
	 */
	public void interpret(JarvisInterpreter ji) {
		AbstractAtom res = interpretNoPut(ji);		
		
		if (!(res instanceof ClosureAtom)) {
			ji.putArg(res);
		}
	}
	

}
