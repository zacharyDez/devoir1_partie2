package jarvis.atoms.primitives.integers;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.IntAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;

//OPERATIONSPRIMITIVES
/*
* Cette classe implante la partie de l'op�ration primitive sp�cifique � la soustraction.
* 
*/

public class IntegerPrimitiveSubtract extends IntegerPrimitiveOperation {
	protected void init() {
		argCount = 1;
	}

	
	@Override
	public String makeKey() {

		return "IntegerPrimitiveSubtract";
	}

	@Override
	protected AbstractAtom calculateResult(JarvisInterpreter ji, IntAtom val1, IntAtom val2) {
		
		// C'est ici que l'op�ration r�elle a lieu
		int total = val1.getValue() - val2.getValue();
		
		// Ici, construit un objet int manuellement
		// � noter, on retourne un objet de type int, pas un atome de type int.
		ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
		data.add(new IntAtom(total));

		return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("int")), data, ji);
	}
}
